# Modelo de Casos de Uso - StudyMatch (Sprint 1)

Este documento especifica o espaço funcional inicial do StudyMatch, abordando a identidade académica, competências, contexto de agrupamento e as duas funcionalidades criativas propostas pela equipa para mitigar o arranque a frio (*cold start*) e atritos de colaboração.

---

## UC01: Registar Histórico e Percurso Académico
* **Objetivo:** Registar e manter o percurso académico do estudante ao longo do curso (unidades curriculares, inscrições, tentativas e classificações).
* **Atores Principais:** Estudante (ou Administrador do Sistema / Serviço Académico).
* **Cenário Principal de Sucesso:**
  1. O ator acede à área de percurso académico do estudante.
  2. O sistema lista o histórico curricular existente (UCs concluídas, em curso e pendentes).
  3. O ator submete o registo de uma ou mais unidades curriculares com ano letivo, semestre, número de tentativas e classificação obtida (0 a 20).
  4. O sistema valida os limites e a coerência das notas submetidas.
  5. O sistema persiste os registos académicos e recalcula a taxa de progressão no curso.
  6. O sistema confirma a atualização com sucesso.
* **Fluxos Alternativos e Exceções:**
  * *4a. Nota Inválida:* Se a nota for inferior a 0 ou superior a 20, o sistema rejeita o registo e devolve mensagem de erro descritiva.
  * *4b. Inscrição Duplicada:* Se já existir uma inscrição concluída com sucesso para a mesma UC, o sistema emite um alerta e impede a duplicação.
* **Regras de Negócio:**
  * Apenas notas iguais ou superiores a 10 valores representam unidades curriculares concluídas.
  * O número de tentativas deve ser um inteiro estritamente positivo (≥ 1).
* **Conceitos de Domínio Revelados:** `Student`, `DegreeCourse`, `CourseUnit`, `Enrollment`, `AcademicRecord`.

---

## UC02: Mapear e Visualizar Perfil de Competências
* **Objetivo:** Estruturar e apresentar a distribuição de competências adquiridas pelo estudante com base nas UCs concluídas e evidências académicas.
* **Atores Principais:** Estudante.
* **Cenário Principal de Sucesso:**
  1. O estudante solicita a visualização do seu perfil de competências.
  2. O sistema recolhe as UCs concluídas e os pesos de competência associados a cada uma.
  3. O sistema calcula a pontuação agregada em diferentes eixos de competência (técnicas, analíticas, metodológicas).
  4. O sistema apresenta o perfil consolidado com as áreas de maior domínio e áreas de menor exposição.
* **Fluxos Alternativos e Exceções:**
  * *2a. Sem Histórico de UCs (Aluno Novo):* Se o estudante não tiver UCs concluídas, o sistema notifica que o perfil académico está em estado inicial e sugere a realização do questionário situacional (UC04).
* **Regras de Negócio:**
  * Cada UC contribui para uma ou mais competências com um fator de peso definido no plano curricular.
  * O nível de competência é cumulativo, mas normalizado numa escala padronizada (ex: 0 a 100%).
* **Conceitos de Domínio Revelados:** `Competency`, `CompetencyWeight`, `StudentProfile`.

---

## UC03: Definir Contexto de Agrupamento
* **Objetivo:** Configurar os parâmetros, restrições e objetivos pedagógicos para a formação de grupos numa atividade curricular.
* **Atores Principais:** Docente (ou Responsável da UC).
* **Cenário Principal de Sucesso:**
  1. O docente seleciona a unidade curricular e cria uma nova atividade de grupo.
  2. O docente define a dimensão alvo do grupo (número mínimo e máximo de membros).
  3. O docente especifica critérios pedagógicos pretendidos (ex: equipas balanceadas em competências técnicas, diversidade de percursos).
  4. O sistema valida os parâmetros e regista o contexto da atividade como ativo para matching.
* **Fluxos Alternativos e Exceções:**
  * *4a. Dimensão Incompatível:* Se a dimensão mínima for superior à máxima ou menor que 2 elementos, o sistema recusa a configuração.
* **Regras de Negócio:**
  * Todo o contexto de formação tem de estar estritamente associado a uma UC ativa.
  * Cada grupo deve ter um tamanho que respeite o intervalo `[minMembers, maxMembers]`.
* **Conceitos de Domínio Revelados:** `GroupingContext`, `GroupingCriteria`, `GroupConstraint`, `TeamActivity`.

---

## UC04 (Criativo 1): Avaliar Perfil Operacional Provisório via Dilemas de Cenário (*Cold Start*)
* **Objetivo:** Resolver o problema do arranque a frio (*cold start*) de novos alunos (sem histórico curricular), inferindo o estilo de trabalho e maturidade colaborativa através de dilemas de decisão rápida de projeto.
* **Atores Principais:** Estudante.
* **Cenário Principal de Sucesso:**
  1. O estudante sem histórico curricular acede ao onboarding do sistema.
  2. O sistema apresenta um conjunto reduzido de dilemas situacionais reais de trabalho de grupo (ex: gestão de prazos apertados, resolução de conflitos, resposta a bugs críticos).
  3. O estudante seleciona a sua linha de ação preferida para cada cenário.
  4. O sistema processa as respostas e gera um perfil operacional provisório (ex: foco na execução, comunicação, resolução analítica).
  5. O sistema marca o perfil como provisório (*Cold Start Seed*), pronto para ser usado nos primeiros matchings.
* **Fluxos Alternativos e Exceções:**
  * *3a. Respostas Incompletas:* O sistema só calcula o perfil provisório se todos os dilemas obrigatórios forem respondidos.
* **Regras de Negócio:**
  * Os dados gerados têm um nível de confiança (*ConfidenceScore*) inferior aos obtidos por evidência académica real.
  * O perfil provisório é progressivamente substituído à medida que o aluno conclui UCs reais.
* **Conceitos de Domínio Revelados:** `ScenarioDilemma`, `SituationalResponse`, `ProvisionalProfile`, `ConfidenceLevel`.

---

## UC05 (Criativo 2): Mapear Janelas de Disponibilidade e Ritmo de Trabalho (*Anti-Friction*)
* **Objetivo:** Recolher os horários operacionais reais e a cadência de entrega do estudante para evitar atritos logísticos severos na formação de grupos.
* **Atores Principais:** Estudante.
* **Cenário Principal de Sucesso:**
  1. O estudante acede à secção de hábitos de trabalho.
  2. O estudante define os blocos semanais em que tem disponibilidade real para reuniões e trabalho em equipa (ex: manhãs, pós-laboral/noites, fins de semana).
  3. O estudante seleciona o seu ritmo de entrega predominante (ex: "Sprint Antecipado" vs "Entrega Contínua" vs "Trabalho Sob Pressão").
  4. O sistema valida e guarda a matriz de sincronismo.
* **Fluxos Alternativos e Exceções:**
  * *2a. Nenhuma Disponibilidade Declarada:* O sistema alerta que a ausência de horários pode prejudicar o emparelhamento com colegas compatíveis.
* **Regras de Negócio:**
  * A disponibilidade semanal mínima declarada deve cobrir pelo menos 4 horas úteis por semana.
  * O ritmo de trabalho serve como critério de compatibilidade (*Friction Score*) durante o algoritmo de agrupamento.
* **Conceitos de Domínio Revelados:** `TimeAvailabilitySlot`, `WorkPacingPreference`, `OperationalSyncProfile`.
