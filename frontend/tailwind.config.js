/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      colors: {
        neon: {
          green: '#00ff88',
          blue: '#00d4ff',
          purple: '#9d4edd',
          pink: '#ff006e',
        },
        chat: {
          bg: '#1E1F24',
          primary: '#8B9DFF',
          bubbleSelf: '#2A2D36',
          bubbleOther: '#252831',
          textPrimary: '#E5E7EB',
          textSecondary: '#9CA3AF',
          accent: '#FCA5A5',
        }
      },
      animation: {
        glow: 'glow 2s ease-in-out infinite',
        pulse: 'pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite',
      },
      keyframes: {
        glow: {
          '0%, 100%': { opacity: '1' },
          '50%': { opacity: '0.5' },
        },
      },
    },
  },
  plugins: [],
}
