import streamingApi from '@root/api/video/videoAxios.ts'
import { useQuery } from '@tanstack/react-query'

export type VideoResolution = '720p' | '480p'

const getSubPlaylist = async (
  videoId: string,
  resolution: VideoResolution,
): Promise<string> => {
  const response = await streamingApi.get<string>(
    `/streaming/${videoId}/playlists/${resolution}`,
    {
      headers: {
        Accept: 'application/x-mpegURL',
      },
      responseType: 'text',
    },
  )

  return response.data
}

export const useGetSubPlaylist = (
  videoId: string,
  resolution: VideoResolution,
) => {
  return useQuery<string, Error>({
    queryKey: ['subPlaylist', videoId, resolution],
    queryFn: () => getSubPlaylist(videoId, resolution),
  })
}
