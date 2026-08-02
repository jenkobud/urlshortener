# Oh My Zsh Agnoster Pastel Colors Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Update the `agnoster` prompt segment background colors to a pastel palette, with user/host sharing the same background color.

**Architecture:** Modify the color configuration variables in `~/.oh-my-zsh/themes/agnoster.zsh-theme` so each segment background uses a pastel 256-color index. Keep segment structure and foreground color logic unchanged, only swapping background values. Use the same pastel background for the user/host context segment.

**Tech Stack:** Zsh, Oh My Zsh (agnoster theme)

---

## File Structure

- Modify: `~/.oh-my-zsh/themes/agnoster.zsh-theme` (theme configuration variables)

## Palette (256-color background indices)

- user/host: 223 (soft peach)
- path: 152 (mint)
- git/bzr/hg: 150 (sage)
- status: 182 (lavender)
- venv/aws/terraform: 217 (rose)

### Task 1: Back up the current theme file

**Files:**
- Modify: `~/.oh-my-zsh/themes/agnoster.zsh-theme`

- [ ] **Step 1: Create a backup**

Run:

```bash
cp ~/.oh-my-zsh/themes/agnoster.zsh-theme ~/.oh-my-zsh/themes/agnoster.zsh-theme.bak
```

Expected: backup file created with identical contents.

- [ ] **Step 2: Commit**

Run:

```bash
git add -N ~/.oh-my-zsh/themes/agnoster.zsh-theme
git commit -m "chore: back up agnoster theme"
```

Expected: commit records the backup intent (even if no content change is staged, confirm repository policy allows this; otherwise skip commit and note in log).

### Task 2: Update segment background colors to pastel palette

**Files:**
- Modify: `~/.oh-my-zsh/themes/agnoster.zsh-theme`

- [ ] **Step 1: Edit theme color variables**

Replace the background color assignments in the configuration block with the pastel palette below:

```zsh
# Current working directory
: ${AGNOSTER_DIR_FG:=${CURRENT_FG}}
: ${AGNOSTER_DIR_BG:=152}

# user@host
: ${AGNOSTER_CONTEXT_FG:=${CURRENT_DEFAULT_FG}}
: ${AGNOSTER_CONTEXT_BG:=223}

# Git related
: ${AGNOSTER_GIT_CLEAN_FG:=${CURRENT_FG}}
: ${AGNOSTER_GIT_CLEAN_BG:=150}
: ${AGNOSTER_GIT_DIRTY_FG:=black}
: ${AGNOSTER_GIT_DIRTY_BG:=150}

# Bazaar related
: ${AGNOSTER_BZR_CLEAN_FG:=${CURRENT_FG}}
: ${AGNOSTER_BZR_CLEAN_BG:=150}
: ${AGNOSTER_BZR_DIRTY_FG:=black}
: ${AGNOSTER_BZR_DIRTY_BG:=150}

# Mercurial related
: ${AGNOSTER_HG_NEWFILE_FG:=white}
: ${AGNOSTER_HG_NEWFILE_BG:=150}
: ${AGNOSTER_HG_CHANGED_FG:=black}
: ${AGNOSTER_HG_CHANGED_BG:=150}
: ${AGNOSTER_HG_CLEAN_FG:=${CURRENT_FG}}
: ${AGNOSTER_HG_CLEAN_BG:=150}

# VirtualEnv colors
: ${AGNOSTER_VENV_FG:=black}
: ${AGNOSTER_VENV_BG:=217}

# AWS Profile colors
: ${AGNOSTER_AWS_PROD_FG:=yellow}
: ${AGNOSTER_AWS_PROD_BG:=217}
: ${AGNOSTER_AWS_FG:=black}
: ${AGNOSTER_AWS_BG:=217}

# Status symbols
: ${AGNOSTER_STATUS_RETVAL_FG:=red}
: ${AGNOSTER_STATUS_ROOT_FG:=yellow}
: ${AGNOSTER_STATUS_JOB_FG:=cyan}
: ${AGNOSTER_STATUS_FG:=${CURRENT_DEFAULT_FG}}
: ${AGNOSTER_STATUS_BG:=182}
```

- [ ] **Step 2: Reload the shell configuration**

Run:

```bash
source ~/.zshrc
```

Expected: prompt re-renders with pastel segment backgrounds.

- [ ] **Step 3: Manual visual verification**

Check:
- All visible prompt segments have pastel backgrounds.
- `user@host` segment background matches the chosen context color (223).

- [ ] **Step 4: Commit**

Run:

```bash
git add ~/.oh-my-zsh/themes/agnoster.zsh-theme
git commit -m "chore: set agnoster prompt to pastel segments"
```

Expected: commit includes the updated background color constants.

---

## Self-Review

- Spec coverage: Background color changes and shared user/host color are fully covered in Task 2.
- Placeholder scan: No TODO/TBD or unspecified steps.
- Type consistency: All variable names match existing theme configuration keys.
