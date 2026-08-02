# Oh My Zsh Agnoster Pastel Segment Colors

## Goal
Update the Oh My Zsh `agnoster` prompt segment background colors to a pastel palette, with user/host segments sharing the same background color.

## Context
The user runs Oh My Zsh with `ZSH_THEME="agnoster"` in `~/.zshrc`. The `agnoster` theme defines segment background colors in `~/.oh-my-zsh/themes/agnoster.zsh-theme` and uses those constants when rendering the prompt.

## Approach
Edit the `agnoster` theme file in place and replace the existing segment background color values with a pastel palette. Keep segment structure, separators, and foreground color logic intact. Assign the same pastel background color to user and host segments.

## Palette (256-color background indices)
- user/host: 223 (soft peach)
- path: 152 (mint)
- git: 150 (sage)
- status: 182 (lavender)
- time: 217 (rose)

## Data Flow
`~/.zshrc` loads Oh My Zsh → Oh My Zsh loads `agnoster.zsh-theme` → prompt rendering uses segment color constants. Only the background color constants change.

## Error Handling
Use numeric 256-color values to avoid unsupported color names. If a terminal does not support 256 colors, the prompt may fall back to default colors; no additional handling is required.

## Testing
- Run `source ~/.zshrc` or open a new shell.
- Confirm all segments show pastel backgrounds.
- Confirm user/host segments share the same background color.

## Out of Scope
- Changing the prompt layout or segment order.
- Changing text/foreground colors beyond what is required for readability.
