import type { Video } from '@root/hooks/common/utils.ts'
import videoApi from '@root/api/video/videoAxios.ts'
import { useQuery } from '@tanstack/react-query'

const getDetailVideo = async (videoId: string): Promise<Video> => {
  const response = await videoApi.get<Video>(`/video/getDetail/${videoId}`)

  return response.data
}

export const useGetDetailVideo = (videoId: string) => {
  return useQuery<Video, Error>({
    queryKey: ['videos', videoId],
    queryFn: () => getDetailVideo(videoId),
  })
}
