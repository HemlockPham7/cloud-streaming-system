import { createBrowserRouter } from 'react-router'
import Navbar from '@root/routes/layout/Navbar.tsx'
import StreamingDashboard from '@root/routes/streaming/StreamingDashboard.tsx'
import StreamingVideoDetail from '@root/routes/streaming/StreamingVideoDetail.tsx'

const routes = createBrowserRouter([
  {
    path: '/',
    Component: Navbar,
    children: [
      {
        index: true,
        Component: StreamingDashboard,
      },
      {
        path: '/videos/:videoId',
        Component: StreamingVideoDetail,
      },
    ],
  },
])

export default routes
