(setq inhibit-splash-screen t)

(global-set-key "\M--" 'shrink-window)
(global-set-key "\M-=" 'enlarge-window)
(global-set-key "\M-g" 'goto-line)
(global-set-key "\C-x," 'previous-error)
(global-set-key "\C-x." 'next-error)
(global-set-key "\C-c\C-v\C-c" 'compile)
(setq-default indent-tabs-mode nil)
(setq-default delete-section-mode t)
(setq scroll-step 1)
(setq global-font-lock-mode t)
(setq transient-mark-mode t)

(defun my-java-mode-hook ()
  (setq c-basic-offset 2))
(add-hook 'java-mode-hook 'my-java-mode-hook)

(setq auto-mode-alist
      (append
       '(("\\.js\\'" . java-mode))
       auto-mode-alist))

(setq auto-mode-alist
      (append
       '(("\\.xml\\'" . html-mode))
       auto-mode-alist))

(custom-set-variables
  ;; custom-set-variables was added by Custom.
  ;; If you edit it by hand, you could mess it up, so be careful.
  ;; Your init file should contain only one such instance.
  ;; If there is more than one, they won't work right.
 '(case-fold-search t)
 '(compilation-ask-about-save nil)
 '(compilation-window-height 7)
 '(compile-command "javac *.java")
 '(gud-gdb-command-name "gdb --annotate=1")
 '(large-file-warning-threshold nil)
 '(require-final-newline t))
(custom-set-faces
  ;; custom-set-faces was added by Custom.
  ;; If you edit it by hand, you could mess it up, so be careful.
  ;; Your init file should contain only one such instance.
  ;; If there is more than one, they won't work right.
 '(mode-line ((((class color) (min-colors 88)) (:foreground "medium blue" :box (:line-width -1 :style released-button)))))
 '(mode-line-inactive ((((class color) (min-colors 88)) (:foreground "medium blue" :box (:line-width -1 :style released-button)))))
 '(region ((((class color) (min-colors 88) (background light)) (:background "blue")))))
