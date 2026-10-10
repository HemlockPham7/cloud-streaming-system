import { Header } from '@root/components'
import { useEffect, useRef, useState } from 'react'
import { useParams } from 'react-router'
import { useGetDetailVideo } from '@root/hooks/video/useGetDetailVideo.ts'
import Hls from 'hls.js'
import { formatCount, formatTimeAgo } from '@root/lib/utils.ts'
import {
  useGetSubPlaylist,
  type VideoResolution,
} from '@root/hooks/streaming/useGetSubPlaylist.ts'

const StreamingVideoDetail = () => {
  const { videoId } = useParams()
  const videoRef = useRef<HTMLVideoElement>(null)

  // Resolution được chọn (Mặc định 720p)
  const [selectedResolution, setSelectedResolution] =
    useState<VideoResolution>('720p')

  // 1. Gọi API lấy thông tin chi tiết Video
  const { data: videoData } = useGetDetailVideo(videoId ?? '')

  // 2. Gọi API lấy Sub Playlist (.m3u8 chứa các file .ts presigned) dựa vào resolution
  const { data: subPlaylistData } = useGetSubPlaylist(
    videoId ?? '',
    selectedResolution,
  )

  // 3. Khởi tạo & load HLS stream từ subPlaylistData (chuỗi m3u8 -> Blob URL)
  useEffect(() => {
    const video = videoRef.current

    if (!video || !subPlaylistData) return

    // Tạo URL từ nội dung .m3u8 mà backend trả về
    const playlistBlob = new Blob([subPlaylistData], {
      type: 'application/vnd.apple.mpegurl',
    })

    const playlistUrl = URL.createObjectURL(playlistBlob)

    let hls: Hls | null = null

    if (Hls.isSupported()) {
      hls = new Hls()

      hls.loadSource(playlistUrl)
      hls.attachMedia(video)

      hls.on(Hls.Events.ERROR, (_event, data) => {
        if (data.fatal) {
          console.error('Fatal HLS error:', data)
        }
      })
    } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
      // Safari hỗ trợ HLS native
      video.src = playlistUrl
    }

    return () => {
      hls?.destroy()

      video.pause()
      video.removeAttribute('src')
      video.load()

      URL.revokeObjectURL(playlistUrl)
    }
  }, [subPlaylistData])

  return (
    <main className='all-users wrapper'>
      <Header
        title='Streaming Video'
        description='Playing the video in 2 resolutions one is 720p and the remaining is 480p'
      />

      <section className='mx-auto max-w-5xl space-y-6'>
        <div className='relative aspect-video w-full overflow-hidden rounded-2xl bg-black shadow-lg'>
          <video
            ref={videoRef}
            controls
            className='size-full object-contain'
            poster={videoData?.thumbnailKey}
          />
        </div>

        <div className='flex items-center justify-between rounded-xl bg-white p-4 shadow-sm'>
          <div>
            <h3 className='text-sm font-semibold text-gray-700'>Resolution</h3>
            <p className='text-xs text-gray-500'>
              Currently playing:{' '}
              <span className='font-bold text-primary-500'>
                {selectedResolution}
              </span>
            </p>
          </div>

          <div className='flex gap-2'>
            <button
              type='button'
              onClick={() => setSelectedResolution('720p')}
              className={`rounded-lg px-4 py-2 text-sm font-medium transition ${
                selectedResolution === '720p'
                  ? 'bg-primary-500 text-white shadow-md'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              720p HD
            </button>

            <button
              type='button'
              onClick={() => setSelectedResolution('480p')}
              className={`rounded-lg px-4 py-2 text-sm font-medium transition ${
                selectedResolution === '480p'
                  ? 'bg-primary-500 text-white shadow-md'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              480p SD
            </button>
          </div>
        </div>

        {videoData && (
          <div className='flex flex-col gap-4 rounded-2xl bg-white p-6 shadow-sm'>
            <div className='flex items-center gap-3 border-gray-100 py-3 text-sm text-gray-500'>
              <div className='flex size-9 items-center justify-center rounded-full bg-primary-50 font-bold uppercase text-primary-500'>
                {videoData.author?.charAt(0) || '?'}
              </div>
              <div className='flex-1'>
                <p className='font-semibold text-gray-800'>
                  {videoData.author}
                </p>
                <p className='text-xs text-gray-400'>
                  Uploaded {formatTimeAgo(videoData.createdAt)}
                </p>
              </div>

              <div className='flex items-center gap-4 text-xs font-medium'>
                <span className='inline-flex items-center gap-1.5'>
                  <svg
                    viewBox='0 0 24 24'
                    fill='none'
                    stroke='currentColor'
                    strokeWidth='1.8'
                    className='size-4'
                    aria-hidden='true'
                  >
                    <path
                      strokeLinecap='round'
                      strokeLinejoin='round'
                      d='M2 12s3.6-7 10-7 10 7 10 7-3.6 7-10 7S2 12 2 12Z'
                    />
                    <circle cx='12' cy='12' r='3' />
                  </svg>
                  <span>{formatCount(videoData.viewCount)} views</span>
                </span>
                <span className='inline-flex items-center gap-1.5'>
                  <svg
                    viewBox='0 0 24 24'
                    fill='none'
                    stroke='currentColor'
                    strokeWidth='1.8'
                    className='size-4'
                    aria-hidden='true'
                  >
                    <path
                      strokeLinecap='round'
                      strokeLinejoin='round'
                      d='M7 10v11H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3Zm0 0 5-8a3 3 0 0 1 2 3l-1 5h5a3 3 0 0 1 3 3l-2 7a3 3 0 0 1-3 2H7'
                    />
                  </svg>
                  <span>{formatCount(videoData.likeCount)} likes</span>
                </span>
              </div>
            </div>

            <div className='flex items-start justify-between gap-4'>
              <div>
                <h1 className='text-xl font-bold text-dark-100'>
                  {videoData.title}
                </h1>
                <p className='mt-1 whitespace-pre-line text-sm text-gray-600 leading-relaxed'>
                  {videoData.description || 'No description provided.'}
                </p>
              </div>

              <div>
                <span className='mb-2 inline-block rounded-md bg-primary-50 px-2.5 py-1 text-xs font-semibold text-primary-600'>
                  {videoData.category}
                </span>
                <span className='rounded-full bg-emerald-50 px-3 py-1 text-xs font-semibold text-emerald-600'>
                  {videoData.status}
                </span>
              </div>
            </div>
          </div>
        )}
      </section>
    </main>
  )
}

export default StreamingVideoDetail
