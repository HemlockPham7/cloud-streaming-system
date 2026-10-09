import type { Video, Pagination } from '@root/hooks/common/utils.ts'
import videoApi from '@root/api/video/videoAxios.ts'
import { useQuery } from '@tanstack/react-query'

export interface GetAllVideosRequest {
  page?: number
  size?: number
  sort?: string
  direction?: 'asc' | 'desc'
  search?: string
}

export interface GetAllVideosResponse {
  data: Video[]
  pagination: Pagination
}

const getAllVideos = async (
  payload: GetAllVideosRequest,
): Promise<GetAllVideosResponse> => {
  const response = await videoApi.get<GetAllVideosResponse>('/video/getAll', {
    params: payload,
  })

  return response.data
}

export const useListVideos = (payload: GetAllVideosRequest) => {
  return useQuery<GetAllVideosResponse, Error>({
    queryKey: ['videos', payload],
    queryFn: () => getAllVideos(payload),
  })
}
