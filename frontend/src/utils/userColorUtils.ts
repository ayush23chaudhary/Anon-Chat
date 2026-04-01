/**
 * User Color Assignment Utility
 * 
 * Generates deterministic, theme-aware colors for anonymous chat users.
 * - Consistent color per userId (deterministic hashing)
 * - Theme-aware (light/dark mode support)
 * - WCAG AA contrast compliant
 * - Soft, comfortable palette for extended viewing
 */

type Theme = 'light' | 'dark';

interface UserColor {
  background: string; // Main color for message bubble
  border: string; // Slightly darker for subtle border
  text: string; // Ensures contrast (usually black or white)
  accent: string; // Optional: for badges or highlights
}

/**
 * Color palette using HSL for better theme adaptation
 * Hues distributed evenly using golden ratio approximation
 * 12 primary colors for good distinction before reuse
 */
const COLOR_PALETTE_HUES = [
  0, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330
] as const;

/**
 * Simple hash function to convert userId to a deterministic number
 * Uses DJB2 algorithm - fast and distributes well across range
 */
function hashUserId(userId: string): number {
  let hash = 5381;
  for (let i = 0; i < userId.length; i++) {
    hash = ((hash << 5) + hash) + userId.charCodeAt(i);
    hash = hash & hash; // Convert to 32bit integer
  }
  return Math.abs(hash);
}

/**
 * Get color index from userId (distributed across palette)
 * Uses modulo to ensure even distribution
 */
function getColorIndex(userId: string): number {
  const hash = hashUserId(userId);
  return hash % COLOR_PALETTE_HUES.length;
}

/**
 * Generate HSL color with theme-aware adjustments
 * 
 * Light theme: Darker colors (L: 50-60%) for better contrast on white
 * Dark theme: Lighter colors (L: 60-70%) for better contrast on dark bg
 */
function generateHSLColor(
  hue: number,
  theme: Theme
): { h: number; s: number; l: number } {
  const saturation = 55; // Moderate saturation - soft but visible
  
  // Lightness adapted per theme
  const lightness = theme === 'light' ? 55 : 65;
  
  return { h: hue, s: saturation, l: lightness };
}

/**
 * Convert HSL to RGB hex format
 * HSL: Hue (0-360), Saturation (0-100), Lightness (0-100)
 */
function hslToHex(h: number, s: number, l: number): string {
  s = s / 100;
  l = l / 100;

  const c = (1 - Math.abs(2 * l - 1)) * s;
  const x = c * (1 - Math.abs(((h / 60) % 2) - 1));
  const m = l - c / 2;

  let r = 0, g = 0, b = 0;

  if (h >= 0 && h < 60) {
    r = c; g = x; b = 0;
  } else if (h >= 60 && h < 120) {
    r = x; g = c; b = 0;
  } else if (h >= 120 && h < 180) {
    r = 0; g = c; b = x;
  } else if (h >= 180 && h < 240) {
    r = 0; g = x; b = c;
  } else if (h >= 240 && h < 300) {
    r = x; g = 0; b = c;
  } else if (h >= 300 && h < 360) {
    r = c; g = 0; b = x;
  }

  const toHex = (val: number) => {
    const hex = Math.round((val + m) * 255).toString(16);
    return hex.length === 1 ? '0' + hex : hex;
  };

  return `#${toHex(r)}${toHex(g)}${toHex(b)}`;
}

/**
 * Calculate relative luminance of a color (for contrast checking)
 * WCAG formula
 */
function getRelativeLuminance(hex: string): number {
  const rgb = parseInt(hex.slice(1), 16);
  const r = (rgb >> 16) & 255;
  const g = (rgb >> 8) & 255;
  const b = rgb & 255;

  const [rs, gs, bs] = [r, g, b].map(val => {
    val = val / 255;
    return val <= 0.03928 ? val / 12.92 : Math.pow((val + 0.055) / 1.055, 2.4);
  });

  return 0.2126 * rs + 0.7152 * gs + 0.0722 * bs;
}

/**
 * Calculate contrast ratio between two colors
 * WCAG formula: (L1 + 0.05) / (L2 + 0.05), where L1 is lighter
 */
function getContrastRatio(foreground: string, background: string): number {
  const l1 = getRelativeLuminance(foreground);
  const l2 = getRelativeLuminance(background);

  const lighter = Math.max(l1, l2);
  const darker = Math.min(l1, l2);

  return (lighter + 0.05) / (darker + 0.05);
}

/**
 * Determine if color should have dark or light text
 * Uses relative luminance to determine text color for accessibility
 * WCAG AA: 4.5:1 minimum contrast for normal text
 */
function getTextColor(backgroundColor: string, theme: Theme): '#000000' | '#FFFFFF' {
  const bgLuminance = getRelativeLuminance(backgroundColor);
  
  // Try both black and white, pick the one with better contrast
  const blackContrast = getContrastRatio('#000000', backgroundColor);
  const whiteContrast = getContrastRatio('#FFFFFF', backgroundColor);

  return whiteContrast > blackContrast ? '#FFFFFF' : '#000000';
}

/**
 * Generate darker shade of a color for border/accent
 * Reduces lightness by 15% for subtle distinction
 */
function getDarkerShade(h: number, s: number, l: number, theme: Theme): string {
  const darkL = theme === 'light' ? Math.max(l - 15, 30) : Math.max(l - 20, 40);
  return hslToHex(h, s, darkL);
}

/**
 * Main function: Generate consistent color set for a user
 * 
 * @param userId - Unique identifier for the user (string)
 * @param theme - Current UI theme ('light' or 'dark')
 * @returns UserColor object with background, border, text, and accent colors
 */
export function getUserColor(userId: string, theme: Theme = 'light'): UserColor {
  // Get deterministic hue from userId
  const colorIndex = getColorIndex(userId);
  const hue = COLOR_PALETTE_HUES[colorIndex];

  // Generate HSL color based on theme
  const { h, s, l } = generateHSLColor(hue, theme);

  // Convert to hex
  const backgroundColor = hslToHex(h, s, l);
  const borderColor = getDarkerShade(h, s, l, theme);
  const textColor = getTextColor(backgroundColor, theme);

  // Lighter variant for accent/badge (if needed)
  const accentL = theme === 'light' ? Math.min(l + 10, 85) : Math.min(l + 15, 90);
  const accentColor = hslToHex(h, s, accentL);

  return {
    background: backgroundColor,
    border: borderColor,
    text: textColor,
    accent: accentColor,
  };
}

/**
 * Verify contrast compliance (debugging/validation)
 * Returns true if WCAG AA standard is met (4.5:1 for normal text)
 */
export function isContrastCompliant(color: UserColor, theme: Theme): boolean {
  const bgLuminance = getRelativeLuminance(color.background);
  const textLuminance = getRelativeLuminance(color.text);

  const lighter = Math.max(bgLuminance, textLuminance);
  const darker = Math.min(bgLuminance, textLuminance);
  const contrast = (lighter + 0.05) / (darker + 0.05);

  return contrast >= 4.5; // WCAG AA standard
}

/**
 * Get all available colors in palette (for reference/visualization)
 */
export function getAllUserColors(theme: Theme = 'light'): UserColor[] {
  return COLOR_PALETTE_HUES.map((hue, index) => {
    const { h, s, l } = generateHSLColor(hue, theme);
    const backgroundColor = hslToHex(h, s, l);
    const borderColor = getDarkerShade(h, s, l, theme);
    const textColor = getTextColor(backgroundColor, theme);
    const accentL = theme === 'light' ? Math.min(l + 10, 85) : Math.min(l + 15, 90);
    const accentColor = hslToHex(h, s, accentL);

    return {
      background: backgroundColor,
      border: borderColor,
      text: textColor,
      accent: accentColor,
    };
  });
}

/**
 * Debug utility: Get color info for a specific user
 */
export function debugUserColor(userId: string, theme: Theme = 'light') {
  const colorIndex = getColorIndex(userId);
  const hue = COLOR_PALETTE_HUES[colorIndex];
  const color = getUserColor(userId, theme);
  const compliant = isContrastCompliant(color, theme);

  return {
    userId,
    colorIndex,
    hue,
    theme,
    ...color,
    wcagCompliant: compliant,
  };
}
