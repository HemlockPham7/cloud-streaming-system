import videoApi from '@root/api/video/videoAxios.ts'
import { useQuery } from '@tanstack/react-query'
import type { Pagination } from '@root/hooks/common/utils.ts'
export interface GetVideoSearchRequest {
  page?: number
  size?: number
  query?: string
}

export interface VideoSearch {
  id: string
  title: string
  description: string
  author: string
  thumbnailKey: string
  thumbnailType: string
  category: string
  viewCount: number
  likeCount: number
  createdAt: string
}

export interface GetVideoSearchResponse {
  data: VideoSearch[]
  pagination: Pagination
}

const getVideoSearch = async (
  payload: GetVideoSearchRequest,
): Promise<GetVideoSearchResponse> => {
  const response = await videoApi.get<GetVideoSearchResponse>(`/video/search`, {
    params: payload,
  })

  return response.data
}

export const useGetVideoSearch = (payload: GetVideoSearchRequest) => {
  return useQuery<GetVideoSearchResponse, Error>({
    queryKey: ['search', payload],
    queryFn: () => getVideoSearch(payload),
  })
}
