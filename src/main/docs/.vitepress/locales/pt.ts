import { defineConfig } from 'vitepress';
import { getVersion } from '../utils';

export default defineConfig({
  description: 'Uma aplicação para ajudar você com a padronização de commits.',
  lang: 'pt-BR',
  themeConfig: {
    logo: {
      src: '/logo.webp',
      innerWidth: 50,
      height: 50,
    },
    nav: [
      { text: 'Home', link: '/pt' },
      { text: 'Guia', link: '/pt/guide/getting-started' },
      {
        text: getVersion(),
        items: [
          { text: 'Changelog', link: '/pt/other/changelog' }
        ],
      },
    ],

    sidebar: [
      {
        text: 'Guia',
        base: '/pt/guide',
        items: [
          { text: 'O que é GitWit?', link: '/what-is' },
          { text: 'Primeiros passos', link: '/getting-started' },
        ],
      },
      {
        text: 'Referência',
        base: '/pt/reference',
        items: [
          { text: 'Configuração', link: '/configuration' },
          { text: 'Comandos', link: '/commands' },
        ],
      },
      {
        text: 'Outros',
        base: '/pt/other',
        collapsed: true,
        items: [
          { text: 'Changelog', link: '/changelog' },
          { text: 'Licença', link: '/license' },
        ],
      },
    ],

    editLink: {
      pattern:
        'https://github.com/rafandoo/gitwit/edit/main/src/main/docs/:path',
      text: 'Edite essa página no GitHub',
    },

    docFooter: {
      prev: 'Anterior',
      next: 'Próximo',
    },

    outline: {
      label: 'Nesta página',
    },

    lastUpdated: {
      text: 'Ultima atualização',
      formatOptions: {
        dateStyle: 'short',
        timeStyle: 'medium',
      },
    },

    footer: {
      message: 'Lançado sob a Licença Apache.',
      copyright:
        'Copyright © 2025-present <a href="https://rafandoo.github.io/">Rafael Camargo</a>',
    },

    search: {
      provider: 'local',
      options: {
        locales: {
          pt: {
            translations: {
              button: {
                buttonText: 'Pesquisar',
                buttonAriaLabel: 'Pesquisar',
              },
              modal: {
                displayDetails: 'Exibir detalhes',
                resetButtonTitle: 'Resultados da pesquisa',
                backButtonTitle: 'Fechar pesquisa',
                noResultsText: 'Nenhum resultado encontrado para',
                footer: {
                  selectText: 'selecionar',
                  selectKeyAriaLabel: 'entrar',
                  navigateText: 'navegar',
                  navigateUpKeyAriaLabel: 'seta para cima',
                  navigateDownKeyAriaLabel: 'seta para baixo',
                  closeText: 'fechar',
                  closeKeyAriaLabel: 'esc',
                },
              },
            },
          },
        },
      },
    },

    langMenuLabel: 'Idioma',
    returnToTopLabel: 'Voltar ao topo',
    sidebarMenuLabel: 'Menu lateral',
    darkModeSwitchLabel: 'Modo escuro',
    lightModeSwitchTitle: 'Alternar para o modo claro',
    darkModeSwitchTitle: 'Alternar para o modo escuro',

    notFound: {
      title: 'Página não encontrada',
      quote:
        'Parece que você está perdido. A página que você está procurando não existe ou foi movida.',
      linkLabel: 'Voltar para a página inicial',
    },
  },
});
