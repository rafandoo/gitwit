# O que é o GitWit?

<br>
<p align="center">
  <picture>
    <img src="/banner.webp" alt="GitWit Logo" width="50%" style="background-color: rgba(255, 255, 255, 0.85); border-radius: 20px; display: inline-block; box-shadow: 0 2px 8px rgba(0,0,0,0.15);">
  </picture>
</p>


**GitWit** é uma aplicação de linha de comando escrita em **Java**, projetada para ajudar desenvolvedores a manterem
seus repositórios Git organizados, coerentes e padronizados.  
Ela atua como um **assistente inteligente de commits**, oferecendo uma interface interativa para criação de mensagens de
commit, geração de changelogs e automação de convenções como o
padrão [Conventional Commits](https://www.conventionalcommits.org/).

## 🧠 O que a aplicação faz?

O GitWit intercepta ou auxilia comandos Git relacionados a commit e changelog e executa ações baseadas em configuração
do projeto. Ele:

- Fornece um **wizard interativo** para criação de commits (com suporte a tipos, escopos e mensagens detalhadas).
- Valida automaticamente mensagens de commit conforme regras definidas (ex: prefixos como `feat`, `fix`, `docs`, etc.).
- Gera changelogs com base no histórico do repositório, organizando entradas por tipo e escopo.
- Permite configuração personalizada de regras, escopos, tipos e dentre outras.

## 🧑‍💻 Para quem é o GitWit?

GitWit é ideal para:

- Desenvolvedores que querem padronizar commits e changelogs em projetos pessoais ou de equipe.
- Equipes que seguem guidelines como **Conventional Commits**, **SemVer**, ou integração com pipelines que dependem
  disso.
- Projetos open-source que desejam facilitar contribuições externas mantendo consistência.

## 🎯 Por que usar o GitWit?

- ✅ Evita erros humanos em mensagens de commit.
- ✅ Garante um histórico de versões limpo e semântico.
- ✅ Aumenta a legibilidade e manutenibilidade do repositório.
- ✅ Automatiza changelogs prontos para releases.
- ✅ Se adapta ao seu projeto — você define os tipos, escopos e validações.
