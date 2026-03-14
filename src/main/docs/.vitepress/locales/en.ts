import { defineConfig } from 'vitepress'
import { getVersion, withBase } from '../utils'

export default defineConfig({
  description: 'An application to help you with commit standardization.',

  themeConfig: {
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Guide', link: '/guide/getting-started' },
      {
        text: getVersion(),
        items: [
          { text: 'Changelog', link: '/other/changelog' },
          { text: 'Releases', link: 'https://github.com/rafandoo/gitwit/releases' },
          { text: 'New Issue', link: 'https://github.com/rafandoo/gitwit/issues/new/choose' },
        ]
      }
    ],

    sidebar: [
      {
        text: 'Guide',
        items: withBase('/guide', [
          { text: 'What is GitWit?', link: '/what-is' },
          { text: 'Getting Started', link: '/getting-started' }
        ]),
      },
      {
        text: 'Reference',
        items: withBase('/reference', [
          {
            text: 'Configuration',
            items: withBase('/configuration', [
              { text: 'Overview', link: '/overview' },
              {
                text: 'Commit', items: withBase('/commit', [
                  { text: 'Commit Types', link: '/types' },
                  { text: 'Scope', link: '/scope' },
                  { text: 'Short Description', link: '/short-description' },
                  { text: 'Long Description', link: '/long-description' },
                  { text: 'Breaking Changes', link: '/breaking-changes' }
                ])
              },
              { text: 'Lint', link: '/lint' },
              { text: 'Changelog', link: '/changelog' },
              {
                text: 'Examples',
                items: withBase('/examples', [
                  { text: 'Basic Example', link: '/basic-example' },
                  { text: 'Emojis', link: '/emoji-example' },
                ]),
              }
            ]),
          },
          {
            text: 'Commands',
            items: withBase('/command', [
              { text: 'Overview', link: '/overview' },
              { text: 'Main Command', link: '/gitwit' },
              { text: 'Commit', link: '/commit' },
              { text: 'Lint', link: '/lint' },
              { text: 'Changelog', link: '/changelog' },
              { text: 'Install', link: '/install' },
              { text: 'Uninstall', link: '/uninstall' },
            ]),
          },
          {
            text: 'Actions',
            items: withBase('/actions', [
              { text: 'Overview', link: '/overview' },
              { text: 'Lint', link: '/lint' },
              { text: 'Changelog', link: '/changelog' },
              { text: 'Examples', link: '/examples' },
            ]),
          }
        ]),
      },
      {
        text: 'Other',
        collapsed: true,
        items: withBase('/other', [
          { text: 'Changelog', link: '/changelog' },
          { text: 'License', link: '/license' },
        ]),
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
      copyright: 'Copyright © 2025-present <a href="https://rafandoo.dev/">Rafael Camargo</a>'
    },
  }
})
