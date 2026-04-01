import React, { useContext } from 'react';
import { getUserColor } from '../utils/userColorUtils';

/**
 * Example: Chat message component using deterministic user colors
 * 
 * This demonstrates how to integrate the color system into your chat UI.
 * Supports both light and dark themes.
 */

// Placeholder for theme context (integrate with your actual theme system)
const ThemeContext = React.createContext('light');

export const ChatMessageWithColor = ({ 
  userId, 
  username, 
  message, 
  timestamp,
  isOwn = false 
}) => {
  const theme = useContext(ThemeContext) || 'light';
  
  // Get deterministic color for this user
  const userColor = getUserColor(userId, theme);

  return (
    <div
      className={`flex ${isOwn ? 'justify-end' : 'justify-start'} mb-4`}
    >
      <div
        className="max-w-xs lg:max-w-md px-4 py-3 rounded-lg shadow-sm border-2"
        style={{
          backgroundColor: userColor.background,
          borderColor: userColor.border,
          color: userColor.text,
        }}
      >
        {/* Username / User Identifier */}
        {!isOwn && (
          <div
            className="text-xs font-semibold mb-1 opacity-80"
            style={{ color: userColor.text }}
          >
            {username || `User ${userId.slice(0, 8)}`}
          </div>
        )}

        {/* Message Content */}
        <div className="text-sm md:text-base break-words">
          {message}
        </div>

        {/* Timestamp */}
        <div
          className="text-xs mt-2 opacity-60"
          style={{ color: userColor.text }}
        >
          {new Date(timestamp).toLocaleTimeString([], {
            hour: '2-digit',
            minute: '2-digit',
          })}
        </div>
      </div>
    </div>
  );
};

/**
 * Alternative: System message (notifications, user joined/left, etc.)
 * Uses the accent color for visual distinction
 */
export const SystemMessageWithColor = ({ 
  message, 
  timestamp 
}) => {
  const theme = useContext(ThemeContext) || 'light';
  
  // Use a neutral gray for system messages
  const systemColor = {
    background: theme === 'light' ? '#f3f4f6' : '#374151',
    border: theme === 'light' ? '#d1d5db' : '#4b5563',
    text: theme === 'light' ? '#374151' : '#e5e7eb',
  };

  return (
    <div className="flex justify-center my-3">
      <div
        className="px-3 py-1 rounded text-xs italic text-center max-w-xs border"
        style={{
          backgroundColor: systemColor.background,
          borderColor: systemColor.border,
          color: systemColor.text,
        }}
      >
        {message}
      </div>
    </div>
  );
};

/**
 * Optional: User avatar/badge with color
 * Useful for showing active users in a list
 */
export const UserBadgeWithColor = ({ 
  userId, 
  username, 
  isOnline = true 
}) => {
  const theme = useContext(ThemeContext) || 'light';
  const userColor = getUserColor(userId, theme);

  return (
    <div className="flex items-center gap-2 p-2 rounded">
      {/* Color dot indicator */}
      <div
        className="w-3 h-3 rounded-full border"
        style={{
          backgroundColor: userColor.background,
          borderColor: userColor.border,
        }}
      />

      {/* Online status */}
      {isOnline && (
        <div className="w-2 h-2 rounded-full bg-green-500" />
      )}

      {/* Username */}
      <span className="text-sm font-medium">
        {username || `User ${userId.slice(0, 8)}`}
      </span>
    </div>
  );
};

/**
 * Color palette preview component (for testing/debugging)
 */
export const ColorPalettePreview = ({ theme = 'light' }) => {
  const { getAllUserColors } = require('../utils/userColorUtils');
  const colors = getAllUserColors(theme);

  return (
    <div className="p-6 rounded-lg border">
      <h3 className="text-lg font-semibold mb-4">
        User Color Palette ({theme} theme)
      </h3>

      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {colors.map((color, index) => (
          <div
            key={index}
            className="p-4 rounded-lg border-2 text-center"
            style={{
              backgroundColor: color.background,
              borderColor: color.border,
              color: color.text,
            }}
          >
            <div className="text-sm font-semibold">Color {index + 1}</div>
            <div className="text-xs opacity-75">
              {color.background}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
