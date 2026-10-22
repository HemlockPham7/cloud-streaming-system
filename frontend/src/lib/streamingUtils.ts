export type ResolutionOption = {
  height: number
  label: string
  uri: string
  bandwidth?: number
}

export const parseMasterPlaylist = (content: string): ResolutionOption[] => {
  const lines = content
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter(Boolean)

  const resolutions: ResolutionOption[] = []

  for (let i = 0; i < lines.length; i++) {
    const line = lines[i]

    if (!line.startsWith('#EXT-X-STREAM-INF:')) {
      continue
    }

    const attributes = line.substring('#EXT-X-STREAM-INF:'.length)

    const resolutionMatch = attributes.match(/RESOLUTION=\d+x(\d+)/i)
    const bandwidthMatch = attributes.match(/(?:AVERAGE-)?BANDWIDTH=(\d+)/i)

    // URI là dòng không phải comment tiếp theo.
    let uri: string | undefined

    for (let j = i + 1; j < lines.length; j++) {
      if (!lines[j].startsWith('#')) {
        uri = lines[j]
        break
      }
    }

    if (!resolutionMatch || !uri) {
      continue
    }

    const height = Number(resolutionMatch[1])

    resolutions.push({
      height,
      label: `${height}p`,
      uri,
      bandwidth: bandwidthMatch ? Number(bandwidthMatch[1]) : undefined,
    })
  }

  // Loại bỏ resolution trùng nhau và sắp xếp từ thấp đến cao.
  return Array.from(
    new Map(resolutions.map((item) => [item.height, item])).values(),
  ).sort((a, b) => a.height - b.height)
}
