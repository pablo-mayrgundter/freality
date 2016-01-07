(setq-default
  c-basic-offset 2
  column-number-mode 1
  fill-column 80
  global-font-lock-mode t
  indent-tabs-mode nil
  inhibit-splash-screen t
  require-final-newline t
  save-place nil
  scroll-step 1
  tab-width 2
  visible-bell nil
  auto-mode-alist (append
    '(("\\.dart\\'" . java-mode))
    '(("\\.js\\'" . java-mode))
    '(("\\.json\\'" . java-mode))
    '(("\\.jsp\\'" . html-mode))
    '(("\\.xml\\'" . html-mode))
    auto-mode-alist))

(global-set-key "\M--" 'shrink-window)
(global-set-key "\M-=" 'enlarge-window)
(global-set-key "\M-g" 'goto-line)
(global-set-key "\C-x," 'previous-error)
(global-set-key "\C-x." 'next-error)
(global-set-key "\C-c\C-v\C-c" 'compile)

(custom-set-variables
  '(compilation-window-height 7 t)
  '(delete-selection-mode t)
  '(menu-bar-mode nil))
(custom-set-faces
  '(mode-line ((t (:foreground "cyan" :inverse-video nil))))
  '(mode-line-inactive ((t (:foreground "cyan" :inverse-video nil)))))
