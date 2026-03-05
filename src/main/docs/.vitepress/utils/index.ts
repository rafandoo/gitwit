import type { RawSidebarItem } from '@/types/RawSidebarItem'

export function getVersion() {
  return process.env.npm_package_version ?? '0.0.0'
}

export function withBase(
  base: string,
  items: RawSidebarItem[]
): any[] {
  return items.map(item => {
    const resolved: any = { ...item }

    if (item.link) {
      resolved.link = `${base}${item.link}`
    }

    if (item.items) {
      resolved.items = withBase(base, item.items)
    }

    return resolved
  })
}
