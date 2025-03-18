import { useEffect } from "react"
import { useNavigate } from "react-router"
import { useAuthStore } from "@/store/auth-storage"

export default function AdminPage() {
  const { hasRole } = useAuthStore()
  const navigate = useNavigate()

  useEffect(() => {
    if (!hasRole("ADMIN")) {
      navigate("/home/me")
    }
  }, [hasRole, navigate])

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold">Admin Dashboard</h1>
      </div>
      <div className="grid gap-4">
        {/* Placeholder content */}
        {[...Array(5)].map((_, i) => (
          <div key={i} className="h-16 bg-white rounded-lg shadow-sm animate-pulse" />
        ))}
      </div>
    </div>
  )
}
