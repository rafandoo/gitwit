import { defineConfig } from 'vitepress';
import { getVersion } from '../utils';

export default defineConfig({
  description: 'An application to help you with commit standardization.',
  lang: 'en-US',
  themeConfig: {
    logo: {
      src: '/logo.webp',
      innerWidth: 50,
      height: 50,
    },
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Guide', link: '/guide/getting-started' },
      {
        text: getVersion(),
        items: [
          { text: 'Changelog', link: '/other/changelog' }
        ]
      }
    ],

    sidebar: [
      {
        text: 'Guide',
        base: '/guide',
        items: [
          { text: 'What is GitWit?', link: '/what-is' },
          { text: 'Getting Started', link: '/getting-started' }
        ]
      },
      {
        text: 'Reference',
        base: '/reference',
        items: [
          { text: 'Configuration', link: '/configuration' },
          { text: 'Commands', link: '/commands' },
        ]
      },
      {
        text: 'Other',
        base: '/other',
        collapsed: true,
        items: [
          { text: 'Changelog', link: '/changelog' },
          { text: 'License', link: '/license' },
        ],
      },
    ],

    editLink: {
      pattern: 'https://github.com/rafandoo/gitwit/edit/main/src/main/docs/:path',
      text: 'Edit this page on GitHub'
    },

    lastUpdated: {
      formatOptions: {
        dateStyle: 'short',
        timeStyle: 'medium',
      }
    },

    footer: {
      message: 'Released under the Apache License.',
      copyright: 'Copyright © 2025-present <a href="https://rafandoo.github.io/">Rafael Camargo</a>'
    },
  }
})
