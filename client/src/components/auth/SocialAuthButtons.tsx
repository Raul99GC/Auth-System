
import { Button } from '@/components/ui/button';
import { useState } from 'react';

import Cookies from 'js-cookie'
import { useNavigate } from 'react-router'

export const SocialAuthButtons = () => {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [token, setToken] = useState<string | null>(null)

  const navigate = useNavigate();

  const handleOAuth = (provider: string): void => {
    const width = 600
    const height = 700
    const left = (window.innerWidth - width) / 2
    const top = (window.innerHeight - height) / 2

    const popupWindow = window.open(
      `${import.meta.env.VITE_BACK_URL}/oauth2/authorization/${provider}`,
      '_blank',
      `width=${width},height=${height},top=${top},left=${left}`
    )

    if (!popupWindow) return

    const interval = setInterval(() => {
      try {
        const currentUrl = popupWindow.location.href
        if (currentUrl.includes('token=')) {
          const token = new URL(currentUrl).searchParams.get('token')
          const refreshToken = new URL(currentUrl).searchParams.get('refreshToken')
          if (token && refreshToken) {
            setToken(token)
            Cookies.set('jwtToken', token, {
              expires: 1, // Dura 7 días
              secure: true, // Solo se envía en conexiones HTTPS
              sameSite: 'Strict', // Evita CSRF
            })
            Cookies.set('refreshToken', token, {
              expires: 1, // Dura 7 días
              secure: true, // Solo se envía en conexiones HTTPS
              sameSite: 'Strict', // Evita CSRF
            })
            popupWindow.close()
            clearInterval(interval)
            window.location.reload();
          }
        }
      } catch (e) {
        if (popupWindow.closed) {
          clearInterval(interval)
        }
      }
    }, 250)
  }

  return (
    <div className="grid grid-cols-1 xl:grid-cols-2 gap-4">
      <Button variant="outline" className="w-full"
        onClick={(e) => {
          e.preventDefault(); // Evita el comportamiento predeterminado
          handleOAuth('google');
        }}
      >
        <svg className="mr-2 h-4 w-4" viewBox="0 0 24 24">
          <path
            d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
            fill="#4285F4"
          />
          <path
            d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
            fill="#34A853"
          />
          <path
            d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
            fill="#FBBC05"
          />
          <path
            d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
            fill="#EA4335"
          />
        </svg>
        with Google
      </Button>
      <Button variant="outline" className="w-full"
        onClick={(e) => {
          e.preventDefault(); // Evita el comportamiento predeterminado
          handleOAuth('github');
        }}
      >
        <svg className="mr-2 h-4 w-4" viewBox="0 0 24 24">
          <path
            fillRule="evenodd"
            clipRule="evenodd"
            d="M12 2C6.477 2 2 6.484 2 12.02c0 4.425 2.865 8.166 6.839 9.504.5.091.682-.217.682-.482 0-.237-.009-.868-.013-1.703-2.782.604-3.369-1.342-3.369-1.342-.454-1.155-1.11-1.463-1.11-1.463-.908-.62.069-.607.069-.607 1.004.07 1.532 1.033 1.532 1.033.892 1.532 2.341 1.09 2.91.834.091-.647.35-1.09.636-1.342-2.22-.253-4.555-1.113-4.555-4.945 0-1.09.39-1.984 1.03-2.682-.103-.253-.447-1.27.098-2.646 0 0 .84-.269 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.295 2.748-1.026 2.748-1.026.547 1.376.203 2.393.1 2.646.64.698 1.03 1.592 1.03 2.682 0 3.84-2.337 4.688-4.566 4.936.359.31.678.92.678 1.855 0 1.337-.012 2.415-.012 2.742 0 .268.18.58.688.482A10.02 10.02 0 0022 12.02C22 6.484 17.523 2 12 2z"
            fill="currentColor"
          />
        </svg>
        with GitHub
      </Button>
    </div>
  );
};
