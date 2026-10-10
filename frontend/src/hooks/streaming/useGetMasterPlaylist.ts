import streamingApi from '@root/api/video/videoAxios.ts'
import { useQuery } from '@tanstack/react-query'

const getMasterPlaylist = async (videoId: string): Promise<string> => {
  const response = await streamingApi.get<string>(
    `/streaming/${videoId}/playlists`,
    {
      headers: {
        Accept: 'application/x-mpegURL',
      },
      responseType: 'text',
    },
  )

  return response.data
}

export const useGetMasterPlaylist = (videoId: string) => {
  return useQuery<string, Error>({
    queryKey: ['masterPlaylist'],
    queryFn: () => getMasterPlaylist(videoId),
  })
}
