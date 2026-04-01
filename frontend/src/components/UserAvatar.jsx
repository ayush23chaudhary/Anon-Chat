import React from 'react';
import { getUserNickname, getNicknameEmoji, getInitials, generateAvatarSVG } from '../utils/userAvatarUtils';

/**
 * User Avatar Component
 * Displays emoji-based or SVG-based avatar with background color
 */
export const UserAvatar = ({ 
  userId, 
  backgroundColor, 
  size = 40,
  variant = 'emoji', // 'emoji' | 'initials'
  className = ''
}) => {
  const nickname = getUserNickname(userId);
  const emoji = getNicknameEmoji(nickname);
  const initials = getInitials(nickname);

  if (variant === 'initials') {
    return (
      <div
        className={`flex items-center justify-center rounded-full font-bold text-white ${className}`}
        style={{
          width: `${size}px`,
          height: `${size}px`,
          backgroundColor,
          fontSize: `${size / 2}px`,
          border: `2px solid ${backgroundColor}`,
        }}
        title={nickname}
      >
        {initials}
      </div>
    );
  }

  // Emoji variant (default)
  return (
    <div
      className={`flex items-center justify-center rounded-full text-center text-white ${className}`}
      style={{
        width: `${size}px`,
        height: `${size}px`,
        backgroundColor,
        fontSize: `${size * 0.6}px`,
        border: `2px solid ${backgroundColor}`,
        color: '#FFFFFF',
      }}
      title={nickname}
    >
      {emoji}
    </div>
  );
};

/**
 * User Badge Component
 * Shows avatar + nickname together
 */
export const UserBadge = ({ 
  userId, 
  backgroundColor, 
  theme = 'light',
  showEmoji = true,
  size = 32,
  className = ''
}) => {
  const nickname = getUserNickname(userId);
  const emoji = getNicknameEmoji(nickname);

  return (
    <div className={`flex items-center gap-2 ${className}`}>
      <UserAvatar 
        userId={userId} 
        backgroundColor={backgroundColor}
        size={size}
        variant="emoji"
      />
      <div className="flex flex-col">
        <span className="text-xs font-bold text-white">{emoji}</span>
        <span className="text-sm font-semibold truncate max-w-[100px] text-white">
          {nickname.split(' ').slice(1).join(' ')}
        </span>
      </div>
    </div>
  );
};

/**
 * Compact User Indicator
 * Small badge for inline display in messages
 */
export const CompactUserIndicator = ({ 
  userId, 
  backgroundColor, 
  size = 24
}) => {
  const nickname = getUserNickname(userId);
  const emoji = getNicknameEmoji(nickname);

  return (
    <div
      className="inline-flex items-center justify-center rounded-full font-bold text-white"
      style={{
        width: `${size}px`,
        height: `${size}px`,
        backgroundColor,
        fontSize: `${size * 0.6}px`,
        color: '#FFFFFF',
      }}
      title={nickname}
    >
      {emoji}
    </div>
  );
};

/**
 * User Info Card
 * Full card showing all user information
 */
export const UserInfoCard = ({ 
  userId, 
  backgroundColor,
  textColor = '#FFFFFF',
  isOnline = true
}) => {
  const nickname = getUserNickname(userId);
  const emoji = getNicknameEmoji(nickname);
  const text = nickname.split(' ').slice(1).join(' ');
  const initials = getInitials(nickname);

  return (
    <div
      className="p-4 rounded-lg border-2 text-center"
      style={{
        backgroundColor,
        borderColor: backgroundColor,
        color: textColor,
      }}
    >
      <div className="text-4xl mb-2">{emoji}</div>
      <div className="font-bold text-sm mb-1">{text}</div>
      <div className="text-xs opacity-75">{initials}</div>
      <div className="mt-2 flex items-center justify-center gap-1">
        <div 
          className="w-2 h-2 rounded-full"
          style={{ backgroundColor: isOnline ? '#10b981' : '#6b7280' }}
        />
        <span className="text-xs">
          {isOnline ? 'Online' : 'Offline'}
        </span>
      </div>
    </div>
  );
};

/**
 * Avatar Gallery Component
 * Display all available avatars (for testing/preview)
 */
export const AvatarGallery = ({ theme = 'light', limit = 12 }) => {
  const colors = [
    '#8B9DFF', '#FF6B9D', '#4ECDC4', '#45B7D1',
    '#FFA07A', '#98D8C8', '#F7DC6F', '#BB8FCE',
    '#85C1E2', '#F8B195', '#C7CEEA', '#B19CD9',
  ];

  return (
    <div className="p-6 border rounded-lg">
      <h3 className="text-lg font-bold mb-4">User Avatar Showcase</h3>
      <div className="grid grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-4">
        {colors.slice(0, limit).map((color, index) => {
          const userId = `user-showcase-${index}`;
          return (
            <div key={index} className="flex flex-col items-center gap-2">
              <UserAvatar 
                userId={userId}
                backgroundColor={color}
                size={48}
                variant="emoji"
              />
              <span className="text-xs text-center">
                {getUserNickname(userId).split(' ').slice(1).join(' ')}
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
};
