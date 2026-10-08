export interface Base {
  id: string
  created_at: string
  updated_at: string
}

export interface User extends Base {
  display_name: string
  username: string
  email: string
}
