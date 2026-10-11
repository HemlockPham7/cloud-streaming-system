import videoApi from '@root/api/video/videoAxios.ts'
import { useQuery } from '@tanstack/react-query'

const getVideoSuggestions = async (prefix: string): Promise<string[]> => {
  const response = await videoApi.get<string[]>(`/video/suggestion`, {
    params: { prefix },
  })

  return response.data
}

export const useGetVideoSuggestions = (prefix: string) => {
  return useQuery<string[], Error>({
    queryKey: ['suggestions', prefix],
    queryFn: () => getVideoSuggestions(prefix),
    enabled: Boolean(prefix && prefix.trim().length > 0),
    staleTime: 1000 * 60,
  })
}
