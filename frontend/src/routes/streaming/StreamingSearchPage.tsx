import { Header } from '@root/components'
import { useNavigate } from 'react-router'
import { type SyntheticEvent, useEffect, useRef, useState } from 'react'
import { useGetVideoSuggestions } from '@root/hooks/video/useGetVideoSuggestions.ts'
import { useGetVideoSearch } from '@root/hooks/video/useGetVideoSearch.ts'
import { formatCount, formatTimeAgo } from '@root/lib/utils.ts'

const StreamingSearchPage = () => {
  const navigate = useNavigate()

  const [searchTerm, setSearchTerm] = useState('')
  const [debouncedPrefix, setDebouncedPrefix] = useState('')
  const [queryToSearch, setQueryToSearch] = useState('')
  const [isDropdownOpen, setIsDropdownOpen] = useState(false)
  const searchRef = useRef<HTMLDivElement>(null)

  const { data: suggestions = [] } = useGetVideoSuggestions(debouncedPrefix)

  const {
    data: searchResult,
    isLoading,
    isError,
  } = useGetVideoSearch({
    page: 0,
    size: 10,
    query: queryToSearch,
  })

  const videos = searchResult?.data ?? []

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        searchRef.current &&
        !searchRef.current.contains(event.target as Node)
      ) {
        setIsDropdownOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedPrefix(searchTerm)
    }, 300)

    return () => {
      clearTimeout(handler)
    }
  }, [searchTerm])

  const handleSearchSubmit = (e: SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault()
    if (!searchTerm.trim()) return
    setQueryToSearch(searchTerm.trim())
    setIsDropdownOpen(false)
  }

  const handleSelectSuggestion = (suggestionText: string) => {
    setSearchTerm(suggestionText)
    setQueryToSearch(suggestionText)
    setIsDropdownOpen(false)
  }

  return (
    <main className='all-users wrapper'>
      <Header
        title='Search Video'
        description='Using auto suggestion for search bar and elasticsearch for returning relevant results to the search data'
      />

      <div className='relative w-full max-w-2xl mx-auto' ref={searchRef}>
        <form onSubmit={handleSearchSubmit} className='flex items-center gap-2'>
          <div className='relative flex-1'>
            <input
              type='text'
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value)
                setIsDropdownOpen(true)
              }}
              onFocus={() => setIsDropdownOpen(true)}
              placeholder='Search videos, authors, categories...'
              className='w-full rounded-xl border border-light-400 bg-white px-4 py-3 pl-11 text-sm text-dark-100 shadow-sm outline-none transition focus:border-primary-500 focus:ring-2 focus:ring-primary-100'
            />
            {/* Search Icon */}
            <svg
              viewBox='0 0 24 24'
              fill='none'
              stroke='currentColor'
              strokeWidth='2'
              className='absolute left-3.5 top-3.5 size-5 text-gray-400'
            >
              <circle cx='11' cy='11' r='8' />
              <path d='m21 21-4.35-4.35' />
            </svg>
          </div>
          <button
            type='submit'
            className='rounded-xl bg-primary-500 px-6 py-3 text-sm font-semibold text-white transition hover:bg-primary-600 shadow-sm'
          >
            Search
          </button>
        </form>

        {/* Suggestion Dropdown List (Tối đa 8 giá trị) */}
        {isDropdownOpen &&
          suggestions.length > 0 &&
          searchTerm.trim() !== '' && (
            <ul className='absolute left-0 right-0 top-full z-20 mt-1 max-h-60 overflow-y-auto rounded-xl border border-light-400 bg-white shadow-lg'>
              {suggestions.slice(0, 8).map((item, index) => (
                <li
                  key={index}
                  onClick={() => handleSelectSuggestion(String(item))}
                  className='flex cursor-pointer items-center gap-3 px-4 py-2.5 text-sm text-dark-100 transition hover:bg-primary-50 hover:text-primary-500'
                >
                  <svg
                    viewBox='0 0 24 24'
                    fill='none'
                    stroke='currentColor'
                    strokeWidth='1.8'
                    className='size-4 text-gray-400'
                  >
                    <circle cx='11' cy='11' r='8' />
                    <path d='m21 21-4.35-4.35' />
                  </svg>
                  <span className='truncate'>{String(item)}</span>
                </li>
              ))}
            </ul>
          )}
      </div>

      {/* Khu vực hiển thị kết quả tìm kiếm */}
      <section className='flex flex-col gap-4 mt-4'>
        {!queryToSearch && (
          <div className='text-center py-12 text-gray-400 text-sm'>
            Type something in the search bar above to find amazing videos.
          </div>
        )}

        {queryToSearch && isLoading && (
          <div className='text-center py-12 text-gray-500 text-sm animate-pulse'>
            Searching for "{queryToSearch}"...
          </div>
        )}

        {isError && (
          <div className='text-center py-12 text-red-500 text-sm'>
            Failed to fetch search results. Please try again.
          </div>
        )}

        {queryToSearch && !isLoading && !isError && videos.length === 0 && (
          <div className='flex flex-col items-center justify-center rounded-2xl border border-dashed border-light-400 bg-white px-6 py-16 text-center'>
            <h3 className='text-lg font-semibold text-dark-100'>
              No results found
            </h3>
            <p className='mt-2 text-sm text-gray-500'>
              We couldn't find any videos matching "{queryToSearch}". Try
              another keyword.
            </p>
          </div>
        )}

        {queryToSearch && !isLoading && !isError && videos.length > 0 && (
          <div className='flex flex-col gap-4 max-w-5xl mx-auto w-full'>
            <div className='text-sm text-gray-500 mb-2'>
              Found {searchResult?.pagination.totalElements ?? videos.length}{' '}
              results for "{queryToSearch}"
            </div>

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
                className='group flex flex-col sm:flex-row gap-4 overflow-hidden rounded-2xl bg-white p-3 shadow-sm border border-light-300 transition duration-300 hover:-translate-y-0.5 hover:shadow-md cursor-pointer'
              >
                <div className='relative w-full sm:w-72 aspect-video shrink-0 overflow-hidden rounded-xl bg-dark-200'>
                  <img
                    src={video.thumbnailKey}
                    alt={video.title}
                    loading='lazy'
                    className='size-full object-cover transition duration-500 group-hover:scale-105'
                  />
                  <span className='absolute bottom-2 right-2 rounded-md bg-black/70 px-2 py-0.5 text-xs font-medium text-white'>
                    {video.category}
                  </span>
                </div>

                <div className='flex flex-col justify-between flex-1 min-w-0 py-1'>
                  <div className='flex flex-col gap-1.5'>
                    <h3
                      title={video.title}
                      className='line-clamp-2 text-lg font-semibold text-dark-100 transition-colors group-hover:text-primary-500 leading-snug'
                    >
                      {video.title}
                    </h3>

                    <div className='flex items-center gap-2 text-xs text-gray-500'>
                      <span>{formatCount(video.viewCount)} views</span>
                      <span>•</span>
                      <span>{formatTimeAgo(video.createdAt)}</span>
                    </div>

                    <div className='flex items-center gap-2 mt-1'>
                      <div className='flex size-6 shrink-0 items-center justify-center rounded-full bg-primary-50 text-xs font-bold uppercase text-primary-500'>
                        {video.author?.charAt(0) || '?'}
                      </div>
                      <span className='text-xs font-medium text-gray-600 truncate'>
                        {video.author}
                      </span>
                    </div>
                  </div>

                  <p className='mt-2 line-clamp-2 text-xs text-gray-500 leading-relaxed'>
                    {video.description}
                  </p>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  )
}

export default StreamingSearchPage
