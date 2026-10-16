
# StudyMatch — Perfil Académico

## Objetivo

O perfil académico representa as competências dos estudantes com base nas classificações obtidas nas unidades curriculares.

## Competências

Nesta primeira versão foram definidas três competências:

- PROGRAMMING — Programação
- DATABASES — Bases de Dados
- SOFTWARE_ENGINEERING — Engenharia de Software

Cada unidade curricular está associada a uma competência principal.

## Regras de cálculo

As classificações académicas variam entre 0 e 20 valores.

A pontuação de uma competência é calculada através da fórmula:

Pontuação = Média das classificações × 5

O resultado é apresentado numa escala de 0 a 100.

Exemplo:

- Nota de Programação: 16
- Pontuação de PROGRAMMING: 16 × 5 = 80

Quando existem várias classificações associadas à mesma competência, é calculada a média aritmética. Cada registo tem o mesmo peso, incluindo eventuais repetições de unidades curriculares.

Exemplo:

- Classificações: 12 e 18
- Média: (12 + 18) / 2 = 15
- Pontuação: 15 × 5 = 75

## Informação em falta

Quando uma competência não possui classificações:

- A pontuação é null.
- O número de evidências é 0.

Uma competência desconhecida não é considerada equivalente a uma pontuação de zero.

## Atualização do perfil

O perfil é calculado sempre que é consultado.

Assim, quando são adicionadas novas classificações ao histórico académico, a consulta seguinte utiliza automaticamente os dados atualizados.

## API REST

GET /api/competencies

Devolve as competências disponíveis.

GET /api/students/{studentNumber}/profile

Devolve o perfil académico calculado do estudante.

Se o estudante não existir, é devolvido HTTP 404.

## Testes

Foram implementados testes unitários com JUnit 5 e Mockito para verificar:

- Cálculo de uma classificação de 16 valores.
- Tratamento de competências sem histórico académico.
- Média de duas classificações.
- Consulta de um estudante inexistente.

## Cold Start

A estratégia Cold Start será implementada separadamente.

Permitirá construir um perfil inicial quando ainda não existe histórico académico suficiente.
