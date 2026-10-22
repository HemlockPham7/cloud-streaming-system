import streamingApi from '@root/api/video/videoAxios.ts'
import { useQuery } from '@tanstack/react-query'

export interface GetStreamingResponse {
  masterPlaylistPath: string
  masterPlaylistContent: string
  basePath: string
}

const getMasterPlaylist = async (
  videoId: string,
): Promise<GetStreamingResponse> => {
  const response = await streamingApi.get<GetStreamingResponse>(
    `/streaming/${videoId}/playlist`,
  )

  return response.data
}

export const useGetMasterPlaylist = (videoId: string) => {
  return useQuery<GetStreamingResponse, Error>({
    queryKey: ['masterPlaylist', videoId],
    queryFn: () => getMasterPlaylist(videoId),
  })
}
