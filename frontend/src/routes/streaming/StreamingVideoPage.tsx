import { useParams } from 'react-router'
import { Header } from '@root/components'
import { useGetMasterPlaylist } from '@root/hooks/streaming/useGetMasterPlaylist.ts'
import { useEffect } from 'react'

const StreamingVideoPage = () => {
  const { videoId } = useParams()

  const { data: videoData } = useGetMasterPlaylist(videoId ?? '')

  useEffect(() => {
    console.log('Data', videoData)
  }, [videoData])

  return (
    <main className='all-users wrapper'>
      <Header
        title='Streaming Video'
        description='Playing the video in 2 resolutions one is 720p and the remaining is 480p'
      />
    </main>
  )
}

export default StreamingVideoPage
