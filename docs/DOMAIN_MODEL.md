# Modelo de Domínio Conceptual - StudyMatch (Sprint 1)

Este documento descreve o modelo conceptual do StudyMatch. Representa as entidades de negócio, agregados, atributos essenciais, regras de integridade e relações, cobrindo o percurso académico, competências, contexto de agrupamento e as extensões criativas de *Cold Start* e sincronização operacional.

---

## 1. Entidades Principais e Conceitos de Valor

### Núcleo Académico e Estudante
* **Student:** Representa o aluno no sistema. Atributos: `studentNumber`, `name`, `institutionalEmail`, `enrolledYear`.
* **DegreeCourse:** O curso/programa curricular em que o estudante está inscrito. Atributos: `code`, `name`, `totalCredits`.
* **CourseUnit:** Unidade curricular (disciplina). Atributos: `code`, `name`, `ectsCredits`, `curricularYear`, `semester`.
* **Enrollment:** Inscrição de um aluno numa UC num determinado ano letivo. Atributos: `academicYear`, `semester`, `status` (Ativa, Concluída, Reprovada).
* **AcademicAttempt:** Tentativa concreta de avaliação dentro de uma inscrição. Atributos: `attemptNumber`, `grade` (0 a 20), `attemptDate`, `isPassed`.

### Competências e Perfil
* **Competency:** Habilidade ou competência mapeável. Atributos: `code`, `name`, `category` (Técnica, Analítica, Metodológica, Humana).
* **CompetencyWeight:** Valor de peso que define a contribuição de uma UC concluída para determinada competência. Atributos: `weightPercentage`.
* **StudentCompetencyProfile:** Vista agregada das capacidades do aluno calculadas pelo sistema. Atributos: `scoreLevel`, `lastCalculatedAt`.

### Contexto de Agrupamento
* **GroupingContext:** Configuração criada por um docente para formar equipas numa UC. Atributos: `contextId`, `title`, `minGroupSize`, `maxGroupSize`, `creationDate`, `deadline`.
* **GroupingCriteria:** Critérios pedagógicos a aplicar na formação dos grupos. Atributos: `criterionType` (Balanceamento, Diversidade), `targetRule`.

### Extensões Criativas (Cold Start & Anti-Atrito)
* **ScenarioDilemma:** Micro-cenário prático de tomada de decisão usado no onboarding. Atributos: `dilemmaId`, `prompt`, `category`.
* **DilemmaOption:** Opção de escolha associada a um perfil operacional. Atributos: `optionId`, `description`, `associatedTrait`.
* **ProvisionalProfile (Cold Start):** Perfil provisório gerado com base nas respostas situacionais. Atributos: `dominantTrait`, `confidenceScore` (0.0 a 1.0).
* **TimeAvailabilitySlot:** Bloco semanal de disponibilidade real para trabalho de equipa. Atributos: `dayOfWeek`, `timePeriod` (Manhã, Tarde, Pós-laboral/Noite), `isAvailable`.
* **WorkPacingProfile:** Preferência de ritmo e cadência de entrega do aluno. Atributos: `pacingType` (Sprint Antecipado, Trabalho Contínuo, Pressão de Prazo).

---

## 2. Diagrama de Classes UML (Conceptual)

```mermaid
classDiagram
    direction TB

    class Student {
        +studentNumber: String
        +name: String
        +institutionalEmail: String
        +enrolledYear: Integer
    }

    class DegreeCourse {
        +code: String
        +name: String
        +totalCredits: Integer
    }

    class CourseUnit {
        +code: String
        +name: String
        +ectsCredits: Integer
        +curricularYear: Integer
        +semester: Integer
    }

    class Enrollment {
        +academicYear: String
        +semester: Integer
        +status: EnrollmentStatus
    }

    class AcademicAttempt {
        +attemptNumber: Integer
        +grade: Decimal
        +attemptDate: Date
        +isPassed: Boolean
    }

    class Competency {
        +code: String
        +name: String
        +category: CompetencyCategory
    }

    class CompetencyWeight {
        +weightPercentage: Decimal
    }

    class StudentCompetencyProfile {
        +scoreLevel: Decimal
        +lastCalculatedAt: DateTime
    }

    class GroupingContext {
        +contextId: String
        +title: String
        +minGroupSize: Integer
        +maxGroupSize: Integer
        +deadline: DateTime
    }

    class GroupingCriteria {
        +criterionType: String
        +targetRule: String
    }

    class ScenarioDilemma {
        +dilemmaId: String
        +prompt: String
        +category: String
    }

    class DilemmaOption {
        +optionId: String
        +description: String
        +associatedTrait: String
    }

    class ProvisionalProfile {
        +dominantTrait: String
        +confidenceScore: Decimal
    }

    class TimeAvailabilitySlot {
        +dayOfWeek: DayOfWeek
        +timePeriod: TimeSlotPeriod
        +isAvailable: Boolean
    }

    class WorkPacingProfile {
        +pacingType: PacingStrategy
    }

    %% Relações Académicas
    Student "1" --> "1" DegreeCourse : inscrito em
    DegreeCourse "1" *-- "1..*" CourseUnit : composto por
    Student "1" *-- "0..*" Enrollment : possui
    CourseUnit "1" <-- "0..*" Enrollment : referente a
    Enrollment "1" *-- "1..*" AcademicAttempt : regista

    %% Relações de Competências
    CourseUnit "1" o-- "0..*" CompetencyWeight : confere
    Competency "1" <-- "0..*" CompetencyWeight : quantifica
    Student "1" *-- "0..*" StudentCompetencyProfile : consolidado em
    Competency "1" <-- "0..*" StudentCompetencyProfile : relativo a

    %% Relações de Contexto de Agrupamento
    CourseUnit "1" *-- "0..*" GroupingContext : define
    GroupingContext "1" *-- "1..*" GroupingCriteria : parametrizado por

    %% Relações de Cold Start
    ScenarioDilemma "1" *-- "2..*" DilemmaOption : disponibiliza
    Student "1" *-- "0..1" ProvisionalProfile : recebe (Cold Start)
    DilemmaOption "1..*" ..> ProvisionalProfile : infere

    %% Relações Operacionais e Ritmo
    Student "1" *-- "0..*" TimeAvailabilitySlot : declara
    Student "1" *-- "0..1" WorkPacingProfile : adota
