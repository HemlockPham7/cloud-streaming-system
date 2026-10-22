import { useParams } from 'react-router'
import { Header } from '@root/components'
import { useGetMasterPlaylist } from '@root/hooks/streaming/useGetMasterPlaylist.ts'
import { useEffect, useMemo, useRef, useState } from 'react'
import { useGetDetailVideo } from '@root/hooks/video/useGetDetailVideo.ts'
import { formatCount, formatTimeAgo } from '@root/lib/utils.ts'
import Hls from 'hls.js'
import { parseMasterPlaylist } from '@root/lib/streamingUtils.ts'

const StreamingVideoPage = () => {
  const { videoId } = useParams()
  const videoRef = useRef<HTMLVideoElement>(null)
  const hlsRef = useRef<Hls | null>(null)

  const [selectedResolution, setSelectedResolution] = useState<number | null>(
    null,
  )

  // 1. Gọi API lấy thông tin chi tiết Video
  const { data: videoData } = useGetDetailVideo(videoId ?? '')

  // 2. Get Streaming Data including master playlist and base path
  const { data: streamingData } = useGetMasterPlaylist(videoId ?? '')

  const resolutions = useMemo(() => {
    if (!streamingData?.masterPlaylistContent) {
      return []
    }

    return parseMasterPlaylist(streamingData.masterPlaylistContent)
  }, [streamingData?.masterPlaylistContent])

  const masterPlaylistUrl = useMemo(() => {
    if (!streamingData?.basePath || !streamingData?.masterPlaylistPath) {
      return null
    }

    const basePath = streamingData.basePath.replace(/\/+$/, '')
    const masterPath = streamingData.masterPlaylistPath.replace(/^\/+/, '')

    return `${basePath}/${masterPath}`
  }, [streamingData?.basePath, streamingData?.masterPlaylistPath])

  // Khởi tạo HLS player và tải master playlist trực tiếp từ CloudFront.
  useEffect(() => {
    const video = videoRef.current

    if (!video || !masterPlaylistUrl) {
      return
    }

    let hls: Hls | null = null

    if (Hls.isSupported()) {
      hls = new Hls({
        // Tự động chọn bitrate dựa trên băng thông đo được.
        capLevelToPlayerSize: true,
        startLevel: -1,
        abrEwmaDefaultEstimate: 1_000_000,
      })

      hlsRef.current = hls

      hls.loadSource(masterPlaylistUrl)
      hls.attachMedia(video)

      hls.on(Hls.Events.MANIFEST_PARSED, () => {
        console.log('HLS manifest loaded')
        console.log('Available levels:', hls?.levels)
      })

      hls.on(Hls.Events.LEVEL_SWITCHED, (_event, data) => {
        const level = hls?.levels[data.level]

        console.log(
          'Current resolution:',
          level?.height ? `${level.height}p` : 'Unknown',
        )
      })

      hls.on(Hls.Events.ERROR, (_event, data) => {
        if (data.fatal) {
          console.error('Fatal HLS error:', data)

          if (data.type === Hls.ErrorTypes.NETWORK_ERROR) {
            hls?.startLoad()
          } else if (data.type === Hls.ErrorTypes.MEDIA_ERROR) {
            hls?.recoverMediaError()
          }
        }
      })
    } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
      // Safari có native HLS và tự động adaptive bitrate.
      video.src = masterPlaylistUrl
    }

    return () => {
      hls?.destroy()

      if (hlsRef.current === hls) {
        hlsRef.current = null
      }

      video.pause()
      video.removeAttribute('src')
      video.load()
    }
  }, [masterPlaylistUrl])

  // Chuyển đổi giữa Auto và độ phân giải được người dùng chọn.
  useEffect(() => {
    const hls = hlsRef.current

    if (!hls || !hls.levels.length) {
      return
    }

    if (selectedResolution === null) {
      // -1 bật lại ABR tự động.
      hls.currentLevel = -1
      return
    }

    const levelIndex = hls.levels.findIndex(
      (level) => level.height === selectedResolution,
    )

    if (levelIndex >= 0) {
      hls.currentLevel = levelIndex
    }
  }, [selectedResolution, resolutions])

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
            playsInline
            className='size-full object-contain'
            poster={videoData?.thumbnailKey}
          />
        </div>

        <div className='flex flex-wrap items-center justify-between gap-4 rounded-xl bg-white p-4 shadow-sm'>
          <div>
            <h3 className='text-sm font-semibold text-gray-700'>
              Video quality
            </h3>

            <p className='text-xs text-gray-500'>
              {selectedResolution === null
                ? 'Automatic quality based on network conditions'
                : `Manually selected: ${selectedResolution}p`}
            </p>
          </div>

          <div className='flex flex-wrap gap-2'>
            <button
              type='button'
              onClick={() => setSelectedResolution(null)}
              className={`rounded-lg px-4 py-2 text-sm font-medium transition ${
                selectedResolution === null
                  ? 'bg-primary-500 text-white shadow-md'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              Auto
            </button>

            {resolutions.map((resolution) => (
              <button
                key={resolution.height}
                type='button'
                onClick={() => setSelectedResolution(resolution.height)}
                className={`rounded-lg px-4 py-2 text-sm font-medium transition ${
                  selectedResolution === resolution.height
                    ? 'bg-primary-500 text-white shadow-md'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                {resolution.label}
              </button>
            ))}
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

export default StreamingVideoPage
