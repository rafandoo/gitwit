import { defineConfig } from 'vitepress'
import pt from './locales/pt'
import en from './locales/en'
import { resolve } from 'node:path'

export default defineConfig({
  vite: {
    resolve: {
      alias: {
        '@': resolve(__dirname, './')
      }
    }
  },

  title: 'GitWit',
  appearance: 'dark',
  lastUpdated: true,
  base: '/gitwit/',
  srcDir: './src',

  head: [
    [ 'link', { rel: 'icon', href: '/gitwit/favicon.ico' } ]
  ],

  cleanUrls: true,

  themeConfig: {
    i18nRouting: true,

    search: {
      provider: 'local'
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/rafandoo/gitwit' }
    ],

    logo: {
      src: '/logo.webp',
      innerWidth: 50,
      height: 50,
    },
  },

  locales: {
    root: {
      label: 'English',
      lang: 'en',
      ...en
    },
    pt: {
      label: 'Português',
      lang: 'pt',
      ...pt
    }
  }
})
