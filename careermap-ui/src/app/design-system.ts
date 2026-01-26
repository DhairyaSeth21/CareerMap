/**
 * UNIFIED DESIGN SYSTEM
 *
 * RULE: ALL pages, overlays, and components MUST use these tokens.
 * NO exceptions. NO one-off colors. NO custom spacing.
 *
 * This ensures visual coherence across:
 * - Landing page
 * - Login/Signup
 * - Calibration
 * - Frontier
 * - All overlays
 */

export const DesignSystem = {
  /**
   * COLORS
   * Dark space theme - identical everywhere
   */
  colors: {
    // Backgrounds
    background: {
      primary: 'from-slate-950 via-indigo-950 to-slate-950',  // Main gradient
      overlay: 'bg-black/80',                                   // Modal backgrounds
      card: 'from-slate-900 to-slate-800',                     // Card/panel backgrounds
      blur: 'backdrop-blur-md',                                 // Blur effect
    },

    // Interactive elements
    primary: {
      gradient: 'from-purple-600 to-pink-600',                 // Main CTA gradient
      hover: 'from-purple-500 to-pink-500',                    // Hover state
      glow: 'bg-purple-500',                                    // Glow effects
    },

    // Text
    text: {
      primary: 'text-white',                                    // Main text
      secondary: 'text-slate-400',                              // Secondary text
      muted: 'text-slate-500',                                  // Muted text
      gradient: 'bg-gradient-to-r from-purple-300 to-pink-300 bg-clip-text text-transparent',  // Hero text
    },

    // Status
    status: {
      success: 'text-green-400',                                // Success/proved
      error: 'text-red-400',                                    // Error/failed
      warning: 'text-yellow-400',                               // Warning
      info: 'text-blue-400',                                    // Info
    },

    // Borders
    border: {
      default: 'border-slate-700',                              // Default border
      active: 'border-purple-500',                              // Active/selected
      glow: 'border-purple-500/50',                             // Glowing border
    },

    // Stars & nodes
    star: {
      domain: 'from-indigo-500 to-purple-600',                  // Domain stars
      role: 'from-purple-500 to-pink-600',                      // Role stars
      node: {
        foundational: 'from-green-500 to-emerald-600',          // Foundational skills
        core: 'from-blue-500 to-indigo-600',                    // Core skills
        advanced: 'from-purple-500 to-pink-600',                // Advanced skills
        specialized: 'from-orange-500 to-red-600',              // Specialized skills
      },
    },
  },

  /**
   * TYPOGRAPHY
   * Consistent font hierarchy
   */
  typography: {
    // Headlines
    h1: 'text-5xl font-bold',                                   // Landing hero
    h2: 'text-4xl font-bold',                                   // Section headers
    h3: 'text-3xl font-bold',                                   // Card headers
    h4: 'text-2xl font-bold',                                   // Subsections
    h5: 'text-xl font-semibold',                                // Small headers

    // Body
    body: {
      large: 'text-lg',                                         // Large body
      base: 'text-base',                                        // Normal body
      small: 'text-sm',                                         // Small body
      xs: 'text-xs',                                            // Extra small
    },

    // Special
    typewriter: 'text-2xl font-light leading-relaxed',         // Typewriter effect
    mono: 'font-mono text-sm',                                  // Code/mono
  },

  /**
   * SPACING
   * Consistent padding/margin
   */
  spacing: {
    xs: 'p-2',           // 8px
    sm: 'p-4',           // 16px
    md: 'p-6',           // 24px
    lg: 'p-8',           // 32px
    xl: 'p-12',          // 48px
    '2xl': 'p-16',       // 64px

    gap: {
      xs: 'gap-2',
      sm: 'gap-4',
      md: 'gap-6',
      lg: 'gap-8',
      xl: 'gap-12',
    },
  },

  /**
   * EFFECTS
   * Consistent shadows, blurs, glows
   */
  effects: {
    glow: {
      sm: 'shadow-[0_0_10px_rgba(168,85,247,0.3)]',
      md: 'shadow-[0_0_20px_rgba(168,85,247,0.4)]',
      lg: 'shadow-[0_0_40px_rgba(168,85,247,0.5)]',
    },
    blur: {
      sm: 'blur-sm',
      md: 'blur-md',
      lg: 'blur-lg',
    },
    shadow: 'shadow-xl',
  },

  /**
   * ANIMATIONS
   * Consistent animation timings
   */
  animations: {
    duration: {
      fast: 'duration-150',
      normal: 'duration-300',
      slow: 'duration-500',
    },
    ease: {
      default: 'ease-in-out',
      spring: 'ease-spring',
    },
  },

  /**
   * LAYOUT
   * Consistent layout patterns
   */
  layout: {
    // Full screen containers
    fullScreen: 'min-h-screen w-full',

    // Centered content
    center: 'flex items-center justify-center',

    // Max widths
    maxWidth: {
      sm: 'max-w-md',
      md: 'max-w-2xl',
      lg: 'max-w-4xl',
      xl: 'max-w-6xl',
    },
  },

  /**
   * BUTTONS
   * Consistent button styles
   */
  buttons: {
    primary: `
      bg-gradient-to-r from-purple-600 to-pink-600
      hover:from-purple-500 hover:to-pink-500
      text-white font-bold
      py-4 px-8 rounded-xl
      transition-all duration-300
      shadow-lg hover:shadow-xl
      disabled:opacity-50 disabled:cursor-not-allowed
    `,
    secondary: `
      bg-slate-800 hover:bg-slate-700
      text-white font-semibold
      py-3 px-6 rounded-lg
      border border-slate-700 hover:border-purple-500
      transition-all duration-300
    `,
    ghost: `
      text-slate-400 hover:text-white
      font-medium
      py-2 px-4 rounded-lg
      hover:bg-slate-800/50
      transition-all duration-300
    `,
  },

  /**
   * OVERLAYS
   * Consistent overlay patterns
   */
  overlays: {
    backdrop: 'fixed inset-0 bg-black/80 backdrop-blur-md z-50',
    modal: `
      bg-gradient-to-br from-slate-900 to-slate-800
      border border-slate-700
      rounded-2xl
      shadow-2xl
    `,
  },
} as const;

/**
 * HELPER FUNCTIONS
 */

export const cn = (...classes: (string | undefined | false)[]) => {
  return classes.filter(Boolean).join(' ');
};

/**
 * COMPONENT PRESETS
 * Common component patterns
 */

export const ComponentPresets = {
  // Starfield background (use everywhere)
  starfield: `
    absolute inset-0
    bg-gradient-to-b ${DesignSystem.colors.background.primary}
  `,

  // Animated star (for domain/role visualization)
  star: `
    rounded-full
    bg-gradient-to-br
    flex items-center justify-center
    transition-all duration-500
    hover:scale-110
  `,

  // Glowing node (for focus node)
  glowingNode: `
    relative
    animate-pulse
    ${DesignSystem.effects.glow.lg}
  `,

  // Command strip (bottom bar)
  commandStrip: `
    fixed bottom-0 left-0 right-0
    bg-slate-900/90 backdrop-blur-lg
    border-t ${DesignSystem.colors.border.default}
    ${DesignSystem.spacing.md}
    z-40
  `,

  // Guidance tooltip
  guidanceTooltip: `
    ${DesignSystem.overlays.modal}
    ${DesignSystem.spacing.md}
    ${DesignSystem.typography.body.small}
    ${DesignSystem.colors.text.secondary}
  `,
};
