
import type React from "react"
import { Link, useLocation } from "react-router"
import { cn } from "@/lib/utils"

interface NavItemProps {
  href: string
  children: React.ReactNode
}

export function NavItem({ href, children }: NavItemProps) {
  const { pathname } = useLocation()
  const isActive = pathname === href

  return (
    <Link
      to={href}
      className={cn(
        "px-4 py-2 rounded-md text-sm font-medium transition-colors",
        isActive ? "bg-blue-500 text-white" : "text-gray-600 hover:text-gray-900 hover:bg-gray-100",
      )}
    >
      {children}
    </Link>
  )
}
