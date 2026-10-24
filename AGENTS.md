# MouseMover

- Stack: Java (bytecode 8), Swing, sem dependências externas
- Build: Gradle 8.5 via wrapper
- Entrypoint: `br.com.atmw.mousemover.MouseMover`

# Comandos essenciais

- Build: `.\gradlew.bat build` (Windows) / `./gradlew build`
- Executar: `java -jar build/libs/MouseMover-1.0.0.jar`
- Testes: não há suíte de testes no projeto

# Arquitetura

Aplicação de arquivo único: toda a interface, a regra de movimento e o
tratamento de erro vivem em `src/main/java/br/com/atmw/mousemover/MouseMover.java`,
em membros estáticos.

# Rules

- NUNCA remover `.classpath`, `.project` ou `.settings/` do disco — eles
  estão rastreados pelo git apesar de constarem no `.gitignore`; tirá-los do
  índice é `git rm --cached`, não exclusão
- SEMPRE conferir o pacote e a linha ao interpretar um stack trace do
  usuário: binários de versões antigas, com o pacote `br.com.atmw` sem o
  segmento `.mousemover`, ainda circulam

<!-- docs-infra:dev-only:start -->
## Dev-Only — Documentação Não-Essencial (remover antes da release)

Documentação de processo deste desenvolvimento — vive fora deste repositório,
em `../MouseMover-docs/`, e não faz parte do artefato final entregue.
Índice: `../MouseMover-docs/INDEX.md`
<!-- docs-infra:dev-only:end -->
