# StudyMatch — Estratégia Cold Start

## 1. Objetivo

A estratégia Cold Start permite criar um perfil inicial para estudantes que ainda não possuem histórico académico.

O objetivo é evitar que estudantes recém-inscritos fiquem sem qualquer representação das suas competências.

Para isso, foi implementado um questionário de autoavaliação.

## 2. Questionário inicial

O questionário é composto por três perguntas, relacionadas com as competências disponíveis no StudyMatch.

| Competência | Pergunta |
|---|---|
| PROGRAMMING | Qual é o teu nível de experiência em programação? |
| DATABASES | Qual é o teu nível de experiência em bases de dados? |
| SOFTWARE_ENGINEERING | Qual é o teu nível de experiência em desenvolvimento de software? |

Cada pergunta permite uma resposta de 1 a 5.

## 3. Regras de cálculo

As respostas são convertidas numa escala de 0 a 100 através da fórmula:

**Pontuação inicial = (Resposta - 1) × 25**

| Resposta | Pontuação |
|---|---|
| 1 | 0 |
| 2 | 25 |
| 3 | 50 |
| 4 | 75 |
| 5 | 100 |

Exemplo: uma resposta de 4 em Programação corresponde a 75 pontos.

Estas pontuações representam autoavaliações, não classificações académicas comprovadas.

## 4. Persistência dos dados

As respostas são guardadas no PostgreSQL através da entidade `ColdStartAnswer`.

Cada resposta contém:

- Um identificador.
- A referência ao estudante.
- A referência à competência.
- A classificação de autoavaliação entre 1 e 5.

Cada estudante pode ter apenas uma resposta por competência.

Se o estudante responder novamente à mesma pergunta, a resposta anterior é atualizada, evitando duplicados.

## 5. Integração com o perfil académico

O sistema utiliza três possíveis origens para a pontuação de cada competência:

**ACADEMIC:** quando existem classificações académicas, a pontuação é calculada com base na média das notas multiplicada por 5.

**COLD_START:** quando não existem notas académicas para a competência, mas existe uma resposta ao questionário, é utilizada a pontuação de autoavaliação.

**UNKNOWN:** quando não existem notas nem respostas, a pontuação é representada por `null`.

A origem da pontuação é identificada através do campo `source`.

O campo `evidenceCount` representa a quantidade de classificações académicas utilizadas. Por isso, uma competência baseada exclusivamente em Cold Start apresenta `evidenceCount: 0`.

## 6. Transição de Cold Start para evidência académica

As classificações académicas têm prioridade sobre as respostas Cold Start.

Por exemplo:

1. Um estudante responde 4/5 em Programação.
2. O perfil apresenta 75 pontos, com origem `COLD_START`.
3. Posteriormente, o estudante obtém 14 valores a Programação.
4. O perfil passa a apresentar 70 pontos, com origem `ACADEMIC`.

A resposta inicial permanece guardada, mas deixa de determinar a pontuação dessa competência enquanto existir informação académica.

Esta estratégia permite construir um perfil inicial e atualizá-lo automaticamente à medida que surgem evidências mais fiáveis.

## 7. API REST

**GET /api/cold-start/questions**

Devolve as perguntas disponíveis, as competências associadas e o intervalo de respostas permitido.

**POST /api/students/{studentNumber}/cold-start**

Guarda ou atualiza uma resposta do estudante.

**GET /api/students/{studentNumber}/cold-start**

Consulta as respostas Cold Start guardadas.

**GET /api/students/{studentNumber}/profile**

Apresenta o perfil calculado, incluindo a pontuação e a origem de cada competência.

## 8. Validações

O sistema verifica:

- Se o estudante existe.
- Se a competência existe.
- Se os campos obrigatórios foram fornecidos.
- Se a resposta está entre 1 e 5.

Respostas inválidas são rejeitadas com HTTP 400. Estudantes ou competências inexistentes originam HTTP 404.

## 9. Testes realizados

Foram implementados testes unitários com JUnit 5 e Mockito para validar o comportamento do sistema, incluindo:

- Registo de uma resposta válida.
- Conversão de uma resposta de 4/5 em 75 pontos.
- Rejeição de respostas inválidas, como 0 e 6.
- Atualização de uma resposta existente.
- Prioridade das classificações académicas sobre o Cold Start.

Foram também realizados testes manuais através da API REST para confirmar o armazenamento, a consulta, a atualização das respostas e a transição para evidências académicas.

## 10. Justificação da estratégia

Foi escolhida a autoavaliação por ser uma solução simples, de rápida implementação e adequada a estudantes sem classificações disponíveis