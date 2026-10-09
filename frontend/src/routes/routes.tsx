import { createBrowserRouter } from 'react-router'
import Navbar from '@root/routes/layout/Navbar.tsx'
import StreamingDashboard from '@root/routes/streaming/StreamingDashboard.tsx'

const routes = createBrowserRouter([
  {
    path: '/',
    Component: Navbar,
    children: [
      {
        index: true,
        Component: StreamingDashboard,
      },
    ],
  },
])

export default routes
