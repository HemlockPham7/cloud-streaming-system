import { createBrowserRouter } from 'react-router'
import Navbar from '@root/routes/layout/Navbar.tsx'
import StreamingDashboard from '@root/routes/streaming/StreamingDashboard.tsx'
import StreamingSearchPage from '@root/routes/streaming/StreamingSearchPage.tsx'
import StreamingVideoPage from '@root/routes/streaming/StreamingVideoPage.tsx'

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
        Component: StreamingVideoPage,
      },
      {
        path: '/videos/search',
        Component: StreamingSearchPage,
      },
    ],
  },
])

export default routes
