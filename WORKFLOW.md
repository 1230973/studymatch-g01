# Governança de Desenvolvimento e Qualidade - StudyMatch

Este documento formaliza as práticas de engenharia de software, fluxo de trabalho no Git/GitHub e critérios de aceitação para o projeto StudyMatch.

---

## 1. Convenção de Ramos (Branching Strategy)

Todo o trabalho deve ser isolado em ramos criados a partir da branch `main`. Commits diretos na `main` estão bloqueados.

### Formato de Nomenclatura:
`<tipo>/#<issue_id>-<descricao-curta>`

* `feat/#<id>-descricao`: Implementação de novas funcionalidades ou componentes funcionais.
* `docs/#<id>-descricao`: Alterações de documentação, use cases ou diagramas de arquitetura.
* `arch/#<id>-descricao`: Tarefas de modelação conceptual, arquitetura ou configuração de persistência.
* `fix/#<id>-descricao`: Resolução de bugs ou incoerências.
* `refactor/#<id>-descricao`: Reestruturação de código sem alteração de comportamento.

*Exemplo:* `docs/#1-workflow-and-governance`

---

## 2. Ciclo de Vida do Trabalho e Rastreabilidade

O ciclo de desenvolvimento segue estritamente a sequência:

1. **Issue:** O trabalho tem de estar representado por uma Issue ativa no GitHub Project Board com a Milestone do Sprint atribuída.
2. **Branch:** Criação do ramo seguindo a convenção, movendo a Issue para a coluna `In Progress`.
3. **Commits:** Mensagens de commit atómicas e em formato claro (ex: `docs: adicionar regras de pull request (#1)`).
4. **Pull Request (PR):**
   * O PR deve referenciar a Issue correspondente no corpo do texto (usando a palavra-chave `Closes #<id>` ou `Resolves #<id>`).
   * A Issue move-se para a coluna `In Review`.
5. **Code Review:**
   * Nenhum membro da equipa pode aprovar o seu próprio Pull Request.
   * É obrigatória a aprovação de pelo menos um outro colega de equipa.
   * Devem ser feitas sugestões ou notas construtivas na revisão antes da aprovação final.
6. **Merge e Limpeza:**
   * Merge para a branch `main` via método *Squash and Merge* ou *Merge Commit*.
   * Eliminação automática/manual do ramo de trabalho após a conclusão.
   * A Issue é fechada e passa para `Done`.

---

## 3. Definition of Done (DoD) - Sprint 1

Um item de trabalho só é considerado `Done` quando cumpre cumulativamente:

- [ ] **Rastreabilidade:** Trabalho associado a uma Issue e integrado via Pull Request revisto por um par.
- [ ] **Qualidade de Código/Documentação:** Segue as convenções de estilo e padrões de nomenclatura acordados pela equipa.
- [ ] **Compilação e Execução:** O código compila sem erros e o esqueleto da aplicação arranca localmente sem falhas.
- [ ] **Documentação Atualizada:** Quaisquer alterações estruturais, decisões ou instruções de arranque estão refletidas nos ficheiros de documentação ou no `README.md`.
- [ ] **Revisão por Pares:** Aprovação formal de pelo menos um revisor no GitHub sem autoaprovação.
- [ ] **Limpeza de Ramos:** O branch associado foi eliminado após o merge com sucesso na `main`.
