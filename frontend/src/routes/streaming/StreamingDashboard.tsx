import { Header } from '@root/components'
import { useListVideos } from '@root/hooks/video/useListVideos.ts'
import { useEffect } from 'react'
import { formatCount, formatTimeAgo } from '@root/lib/utils.ts'
import { useNavigate } from 'react-router'

const StreamingDashboard = () => {
  const navigate = useNavigate()

  const { data, isLoading, isError } = useListVideos({
    page: 0,
    size: 10,
  })

  useEffect(() => {
    console.log('data from endpoint: ', data)
  }, [data])

  const videos = data?.data ?? []

  return (
    <main className='all-users wrapper'>
      <Header
        title='Streaming Video Dashboard'
        description='Show all the videos for streaming'
      />

      <section className='flex flex-col gap-6'>
        {/* Empty state */}
        {!isLoading && !isError && videos.length === 0 && (
          <div className='flex flex-col items-center justify-center rounded-2xl border border-dashed border-light-400 bg-white px-6 py-16 text-center'>
            <div className='mb-4 flex size-16 items-center justify-center rounded-full bg-primary-50 text-3xl'>
              ▶
            </div>
            <h3 className='text-lg font-semibold text-dark-100'>
              No videos yet
            </h3>
            <p className='mt-2 text-sm text-gray-500'>
              Uploaded videos will appear here.
            </p>
          </div>
        )}

        {/* Video grid */}
        {!isLoading && !isError && videos.length > 0 && (
          <div className='grid grid-cols-1 gap-6 sm:grid-cols-2 xl:grid-cols-3'>
            {videos.map((video) => (
              <article
                key={video.id}
                onClick={() =>
                  navigate(`/videos/${video.id}`, {
                    state: { video },
                  })
                }
                role='link'
                tabIndex={0}
                className='group overflow-hidden rounded-2xl bg-white shadow-400 transition duration-300 hover:-translate-y-1 hover:shadow-200'
              >
                {/* Thumbnail */}
                <div className='relative aspect-video overflow-hidden bg-dark-200'>
                  <img
                    src={video.thumbnailKey}
                    alt={video.title}
                    loading='lazy'
                    className='size-full object-cover transition duration-500 group-hover:scale-105'
                  />

                  <div className='absolute inset-0 bg-gradient-to-t from-black/50 via-transparent to-transparent' />

                  <span className='absolute left-3 top-3 rounded-md bg-black/60 px-2.5 py-1 text-xs font-semibold text-white backdrop-blur-sm'>
                    {video.status}
                  </span>

                  <span className='absolute bottom-3 right-3 rounded-md bg-black/70 px-2 py-1 text-xs font-medium text-white'>
                    {video.category}
                  </span>
                </div>

                {/* Video information */}
                <div className='flex flex-col gap-4 p-4'>
                  <div className='flex min-w-0 items-start gap-3'>
                    <div className='flex size-10 shrink-0 items-center justify-center rounded-full bg-primary-50 text-sm font-bold uppercase text-primary-500'>
                      {video.author?.charAt(0) || '?'}
                    </div>

                    <div className='min-w-0 flex-1'>
                      <h3
                        title={video.title}
                        className='line-clamp-2 text-base font-semibold leading-6 text-dark-100 transition-colors group-hover:text-primary-500'
                      >
                        {video.title}
                      </h3>

                      <p className='mt-1 truncate text-sm text-gray-500'>
                        {video.author}
                      </p>
                    </div>
                  </div>

                  <div className='flex flex-wrap items-center gap-x-4 gap-y-2 border-t border-light-300 pt-3 text-sm text-gray-500'>
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
                      <span>{formatCount(video.viewCount)} views</span>
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
                      <span>{formatCount(video.likeCount)} likes</span>
                    </span>
                  </div>

                  <div className='flex items-center justify-between gap-3'>
                    <span className='text-xs text-gray-500'>
                      Uploaded {formatTimeAgo(video.createdAt)}
                    </span>

                    <span className='size-2 shrink-0 rounded-full bg-success-500' />
                  </div>
                </div>
              </article>
            ))}
          </div>
        )}

        {/* Pagination summary */}
        {!isLoading && !isError && videos.length > 0 && (
          <div className='flex flex-col gap-1 border-t border-light-400 pt-4 text-sm text-gray-500 sm:flex-row sm:items-center sm:justify-between'>
            <span>
              Showing {videos.length} of{' '}
              {data?.pagination.totalElements ?? videos.length} videos
            </span>
            <span>
              Page {(data?.pagination.page ?? 0) + 1} of{' '}
              {data?.pagination.totalPages ?? 1}
            </span>
          </div>
        )}
      </section>
    </main>
  )
}

export default StreamingDashboard
