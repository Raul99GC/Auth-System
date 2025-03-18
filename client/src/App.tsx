import { useEffect } from 'react';
import './App.css'
import Navigation from './navigation/Navigation'
import { useAuthStore } from './store/auth-storage';

function App() {

  const { checkAuth } = useAuthStore();

  useEffect(() => {
    checkAuth();
  }, [checkAuth]);

  return (
    <>
      <Navigation />
    </>
  )
}

export default App
