import { createBrowserRouter } from 'react-router'
import Navbar from '@root/routes/layout/Navbar.tsx'

const routes = createBrowserRouter([
  {
    path: '/',
    Component: Navbar,
  },
])

export default routes
