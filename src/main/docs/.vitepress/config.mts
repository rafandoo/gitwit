import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vitepress'
import pt from './locales/pt'
import en from './locales/en'

export default defineConfig({
  vite: {
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./', import.meta.url)),
      }
    }
  },
  title: "GitWit",
  appearance: "dark",
  lastUpdated: true,
  base: '/gitwit/',
  head: [
    ['link', { rel: 'icon', href: '/gitwit/favicon.ico' }]
  ],
  themeConfig: {
    search: {
      provider: 'local'
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/rafandoo/gitwit' }
    ],
  },
  locales: {
    root: {
      label: 'English',
      ...en
    },
    pt: {
      label: 'Português',
      ...pt
    }
  }
})
