# StudyMatch — Sprint 3: desenho técnico e decisões de arquitetura

**Estado:** proposta de arquitetura (Issue #23) — **ainda não implementada**  
**Prazo do Sprint:** 6 de dezembro de 2026  
**Responsável pela Issue:** Diogo Nunes  
**Revisão:** João Rodrigues e Tomás  
**Base:** funcionalidades existentes dos Sprints 1 e 2 e enunciado «StudyMatch — Sprint 3».

> **Nota:** as regras mínimas de grupos, os ensaios de qualidade e os entregáveis vêm do enunciado do Sprint 3. Os nomes das novas classes, endpoints, algoritmo e decisões de segurança descritos abaixo são **propostas da equipa**, a validar antes da implementação.

## 1. Objetivo e âmbito

Concluir o StudyMatch, permitindo que um professor selecione estudantes elegíveis para uma atividade, gere grupos automaticamente com base nos perfis académicos e no Cold Start, consulte os resultados e compreenda a lógica da distribuição. A aplicação deverá também distinguir as permissões de aluno e professor.

O Sprint inclui obrigatoriamente testes JUnit de regressão e da lógica nova, testes Postman com asserções, ensaios JMeter, cobertura JaCoCo, análise SonarQube, pelo menos duas melhorias justificadas, análise OWASP ZAP, teste manual de segurança, documentação e GitHub Release.

A importação de históricos por CSV é uma melhoria desejada após o Sprint 2, **não um requisito obrigatório explícito** do enunciado do Sprint 3; deve ser priorizada apenas sem comprometer os requisitos obrigatórios.

## 2. Estado atual e reutilização

Elementos existentes a reutilizar:

- `Student`, `DegreeCourse`, `CourseUnit` e `AcademicRecord` — dados e histórico académico.
- `Competency` e `ColdStartAnswer` — competências e questionário inicial.
- `StudentProfileService.getStudentProfile(studentNumber)` — pontuações de competências e respetiva origem (`ACADEMIC`, `COLD_START`, `UNKNOWN`).
- Spring Boot, Spring Data JPA, PostgreSQL, React/Vite, Maven, JUnit 5 e Mockito.
- Fluxo GitHub `Issue → Branch → Implementação → Pull Request → Review → Merge`.

**Ainda por implementar:** autenticação, autorização de alunos/professores, contextos de geração, seleção de elegíveis, matching, persistência e consulta de grupos, interface de gestão de grupos e a extensão de produto escolhida.

## 3. Atores e permissões propostas

### 3.1 Professor (`ROLE_TEACHER`)

- Autenticar-se e terminar sessão.
- Gerir estudantes e registar classificações.
- Criar contextos de formação e selecionar estudantes elegíveis.
- Gerar grupos, consultar gerações anteriores e ver os indicadores de equilíbrio.

### 3.2 Aluno (`ROLE_STUDENT`)

- Autenticar-se e terminar sessão.
- Consultar **apenas os próprios** dados, histórico académico, perfil e grupos atribuídos.
- Submeter/atualizar **apenas as próprias** respostas Cold Start.
- Não criar contextos, não gerar grupos e não registar classificações.

### 3.3 Regras de segurança

- Proposta: Spring Security com sessões autenticadas, cookies `HttpOnly` e `SameSite`, proteção CSRF em pedidos de alteração e configuração CORS apropriada para o frontend.
- Palavras-passe armazenadas com hashing adaptativo (`BCrypt`), nunca em texto simples. Não guardar credenciais reais no Git.
- As permissões são obrigatoriamente verificadas **no backend**, não só pela ocultação de botões no React.
- Em endpoints de dados privados, verificar também **titularidade**: um aluno não pode mudar o identificador na URL e consultar dados de outro aluno.
- Não permitir registo público de contas com papel de professor; contas docentes são criadas por um processo autorizado.
- A realização de uma auditoria de segurança e a configuração exata serão validadas com OWASP ZAP e testes manuais.

## 4. Modelo de domínio proposto

| Entidade | Campos principais | Relações e responsabilidade |
|---|---|---|
| `UserAccount` | `id`, `username/email` único, `passwordHash`, `role`, `enabled` | Uma conta de aluno fica ligada a um `Student`; professor pode não ter `Student` associado |
| `GroupFormationContext` | `id`, `name`, `description`, `createdAt`, `createdBy` | Define a atividade académica para a qual serão formados grupos |
| `ContextParticipant` | `id`, `context`, `student` | Liga estudantes elegíveis ao contexto; combinação `(context, student)` única |
| `GroupGeneration` | `id`, `context`, `generatedAt`, `algorithmVersion`, `strategy`, `parameters` | Regista **cada execução**, sem apagar as anteriores |
| `StudentGroup` | `id`, `generation`, `groupNumber`, `balanceScore`, `explanation` | Um dos grupos de uma geração; número único por geração |
| `GroupMembership` | `id`, `generation`, `studentGroup`, `student` | Atribuição do estudante a um grupo; `(generation, student)` único |

### 4.1 Relações

- Um `GroupFormationContext` pode ter muitos `ContextParticipant` e muitas `GroupGeneration`.
- Um `ContextParticipant` representa um único estudante elegível num contexto.
- Uma `GroupGeneration` contém vários `StudentGroup`.
- Um `StudentGroup` contém vários `GroupMembership`.
- Cada `GroupMembership` associa um `Student` a **uma única** `GroupGeneration` e a um `StudentGroup` dessa geração.
- `GroupMembership.generation` deve corresponder à geração do respetivo `studentGroup`; validar esta consistência ao guardar, além da restrição de unicidade.
- Conservar as gerações anteriores para permitir consulta e comparação histórica.

### 4.2 Integridade da persistência

- Restrições de unicidade em contexto/estudante, geração/número de grupo e geração/estudante.
- Operação de geração e gravação realizada numa única transação (`@Transactional`): ou todos os grupos/membros são guardados ou nenhum é.
- Guardar o instante exato, a versão da estratégia e o resultado/explicação da geração. Se as notas mudarem depois, as atribuições antigas permanecem consultáveis.
- A convenção para snapshots de competências na geração deve ser definida antes da implementação, para que relatórios antigos não apresentem uma justificação contraditória com dados atualizados posteriormente.

## 5. Regras de formação de grupos (obrigatórias)

1. Considerar **todos** os estudantes selecionados como elegíveis e validados.
2. Cada estudante deve aparecer **uma única vez** por geração.
3. Um estudante não pode aparecer duplicado num grupo.
4. Os grupos devem ter **4 ou 5 estudantes quando matematicamente possível**.
5. Configurações inválidas/impossíveis devem originar erro claro e não uma atribuição parcial silenciosa.
6. Guardar os grupos gerados e permitir a consulta posterior.
7. Mostrar membros, informação de perfil relevante e uma explicação relacionada com a estratégia.

### 5.1 Distribuição de tamanhos

Para `n` elegíveis, procurar inteiros não negativos `a` e `b` tais que `4a + 5b = n`, onde `a` é o número de grupos de 4 e `b` o número de grupos de 5. Só depois distribuir os estudantes.

| Estudantes | Distribuição válida proposta |
|---:|---|
| 4 | 4 |
| 5 | 5 |
| 8 | 4 + 4 |
| 9 | 5 + 4 |
| 10 | 5 + 5 |
| 12 | 4 + 4 + 4 |
| 23 | 5 + 5 + 5 + 4 + 4 |

Exemplos de totais sem solução com grupos exclusivamente de 4 ou 5: `0`, `1`, `2`, `3`, `6`, `7`, `11`. **Decisão proposta:** rejeitar com HTTP 422 (ou 400, a uniformizar com a API) e mensagem descritiva; não gerar grupos incompletos. A escolha do código de resposta deve ser consistente e coberta por testes.

Para totais com várias distribuições possíveis, definir um desempate estável, por exemplo escolher a distribuição com o menor número total de grupos, usando grupos de 5 quando possível.

## 6. Matching por competências (proposta a validar com João)

### 6.1 Fontes de informação

- Obter o perfil de cada estudante através do `StudentProfileService`, reutilizando as regras do Sprint 2.
- `ACADEMIC`: pontuação calculada a partir das classificações; prioridade máxima para essa competência.
- `COLD_START`: pontuação provisória de autoavaliação, usada na ausência de notas.
- `UNKNOWN`: ausência de informação (`null`). **Não interpretar `null` como zero.**

### 6.2 Objetivo de matching

Criar grupos cujos perfis globais sejam relativamente equilibrados e que, quando possível, incluam competências complementares. Definir uma função simples, determinística e testável, sem prometer otimização global perfeita.

**Estratégia inicial sugerida:**

1. Validar e remover/rejeitar identificadores repetidos na seleção, conforme contrato da API.
2. Calcular quantos grupos de 4 e 5 são possíveis.
3. Obter os perfis dos participantes.
4. Ordenar estudantes por um indicador de competência disponível, sem penalizar automaticamente os `UNKNOWN` como zero.
5. Distribuir por uma heurística equilibrada de atribuição, respeitando os tamanhos definidos.
6. Calcular um indicador de equilíbrio e uma explicação breve por grupo.
7. Validar os invariantes finais antes de persistir.

**Decisões a fechar com João antes de programar:** fórmula concreta do indicador de equilíbrio; tratamento estatístico de competências desconhecidas; pesos entre equilíbrio e complementaridade; desempate e reprodutibilidade (sem aleatoriedade ou com seed registada).

## 7. Endpoints REST propostos (contrato preliminar)

| Método | Endpoint | Papel | Finalidade |
|---|---|---|---|
| `POST` | `/api/auth/login` | Público | Iniciar sessão, se for adotado controller explícito |
| `POST` | `/api/auth/logout` | Autenticado | Terminar sessão |
| `GET` | `/api/auth/me` | Autenticado | Obter utilizador e papel atual |
| `POST` | `/api/grouping-contexts` | Professor | Criar contexto |
| `GET` | `/api/grouping-contexts` | Professor | Listar contextos autorizados |
| `PUT` | `/api/grouping-contexts/{id}/participants` | Professor | Definir elegíveis |
| `POST` | `/api/grouping-contexts/{id}/generations` | Professor | Gerar e guardar grupos |
| `GET` | `/api/grouping-contexts/{id}/generations` | Professor | Listar gerações |
| `GET` | `/api/grouping-contexts/{id}/generations/{generationId}` | Professor | Consultar grupos/membros/explicações |
| `GET` | `/api/me/groups` | Aluno | Consultar os próprios grupos atribuídos |

Este quadro não significa que os endpoints já existam. O contrato final deve incluir os DTOs, os códigos HTTP e as respostas de erro padronizadas.

## 8. Interface React proposta

### Professor

1. Login como professor.
2. Ver estudantes e seus perfis.
3. Criar contexto com nome/descrição.
4. Selecionar estudantes elegíveis numa lista com pesquisa/filtros existentes.
5. Gerar grupos e visualizar cada grupo, membros, competências e justificação.
6. Consultar gerações anteriores e respetivos indicadores.

### Aluno

1. Login como aluno.
2. Consultar **apenas o próprio** histórico, perfil e autoavaliação.
3. Consultar a atribuição ao grupo, quando disponível.

Mensagens de erro claras para login incorreto, falta de permissão, dados incompletos e quantidades de estudantes incompatíveis.

## 9. Funcionalidade adicional da equipa

**Escolha proposta:** indicador de equilíbrio de grupos, baseado nos dados de competências efetivamente disponíveis, acompanhado de breve explicação (por exemplo, grupos com médias próximas em Programação/Bases de Dados/Engenharia de Software). O cálculo e o alcance do indicador terão de ser especificados pelo João e confirmados com testes.

**Outra evolução desejada (prioridade menor):** importação de histórico CSV com validações e deteção de duplicados; não assumir integração com sistemas internos da universidade nem acesso a bases externas.

O sistema de login e papéis é igualmente uma extensão do produto. Não é necessário implementar várias extensões adicionais complexas se isso prejudicar o mínimo exigido.

## 10. Plano de testes e validação

### 10.1 JUnit 5

- Manter e executar os testes existentes do Sprint 2 como regressão.
- Testar distribuição para 4, 5, 8, 9, 10 e 23 estudantes.
- Testar quantidades impossíveis, lista vazia, duplicados e participantes inelegíveis.
- Testar pertença única por geração e persistência atómica.
- Testar a estratégia de matching com perfis académicos, Cold Start e `UNKNOWN`.
- Testar permissões: aluno não gera grupos, não altera notas e não acede ao perfil de outro aluno.

### 10.2 Postman (obrigatório)

- Exportar Collection com asserções automatizadas: operação válida, inválida, persistência, fluxo completo multi-pedido e geração/consulta de grupos.
- Incluir variáveis para identificadores e, se o login estiver ativo, autenticação e permissões.

### 10.3 JMeter (obrigatório)

- Definir endpoint relevante, baseline e load test.
- Documentar concorrência, ramp-up e iterações/duração.
- Recolher média, mediana, p95, throughput e taxa de erros, comparando os resultados.

### 10.4 Métricas (obrigatório)

- JaCoCo: Line Coverage e Branch Coverage, com interpretação.
- SonarQube Community Build: complexidade ciclomática, duplicação (%) e problemas de manutenção.
- Mais uma métrica da equipa, como número de testes automatizados, com justificação.
- Identificar duas melhorias com `Problema → Evidência → Modificação → Verificação`, pelo menos uma de desenho/manutenção.

### 10.5 Segurança (obrigatório)

- OWASP ZAP: análise automatizada da aplicação/API e interpretação de pelo menos um alerta relevante.
- Teste manual orientado à segurança: por exemplo, verificar que aluno não consegue aceder a dados de outro aluno nem gerar grupos. Documentar esperado/observado/conclusão.

## 11. Organização e dependências

| Responsável proposto | Principais tarefas |
|---|---|
| Diogo | Documentação/arquitetura (Issue #23), modelo de grupos, persistência, controllers/API, Postman |
| João Rodrigues | Algoritmo de matching, regras de equilíbrio, testes JUnit, JaCoCo, SonarQube |
| Tomás | Frontend, autenticação/fluxos com apoio no backend, JMeter, OWASP ZAP |
| Equipa | Revisões, integração de autenticação com API, duas melhorias, documentação, `CHANGELOG.md`, release |

**Ordem de integração sugerida:**

1. Fechar contratos de domínio, seleção de elegíveis, algoritmo e permissões.
2. Implementar backend de contextos e persistência; iniciar matching/testes em paralelo.
3. Integrar backend de autenticação e permissões antes da versão final do frontend.
4. Ligar React à API e verificar fluxo completo.
5. Executar testes Postman/JUnit, métricas e segurança; corrigir problemas; repetir testes.
6. Fazer a release final com instruções reproduzíveis e notas de versão.

## 12. Critérios de aceitação da Issue #23

- [ ] Modelo de domínio e relações de persistência definidos e revistos.
- [ ] Regras de participantes/grupos e casos impossíveis formalizados.
- [ ] Estratégia de matching documentada, incluindo Cold Start e `UNKNOWN`.
- [ ] Matriz de permissões aluno/professor definida.
- [ ] Contratos preliminares de API e ecrãs documentados.
- [ ] Funcionalidade extra delimitada.
- [ ] Trabalho de implementação e qualidade desdobrado em Issues dos três elementos.
- [ ] Confirmar com a docente a composição da equipa (o enunciado indica 4–5 estudantes, mas a equipa atual tem três).

**Próximo passo:** discutir e fechar as decisões pendentes com João e Tomás, atualizar este documento e pedir review num Pull Request associado à Issue #23.
