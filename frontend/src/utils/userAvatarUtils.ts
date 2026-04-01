/**
 * User Avatar & Nickname Generation Utility
 * 
 * Generates deterministic, fun animal nicknames and avatars for anonymous users.
 * Same user always gets same nickname and avatar across sessions.
 */

type Theme = 'light' | 'dark';

export interface UserIdentity {
  nickname: string;
  avatar: string; // Emoji or SVG data
  avatarBg: string; // Background color for avatar
  initials: string;
}

/**
 * Collection of animal nicknames with fun, memorable names
 * 50+ animals for great variety, with Roman numeral variants for even more options
 * This creates 150+ unique combinations before collision
 */
const ANIMAL_NICKNAMES = [
  '🦊 Fox',
  '🐺 Alpha Wolf',
  '🥷 Ninja Panda',
  '🦁 Lion King',
  '🐯 Tiger Strike',
  '🐻 Bear Force',
  '🦅 Eagle Eye',
  '🦉 Wise Owl',
  '🐢 Turtle Shell',
  '🦈 Shark Bite',
  '🐉 Dragon Fire',
  '🦅 Phoenix Rise',
  '🐺 Shadow Wolf',
  '🦌 Swift Deer',
  '🦊 Sly Fox',
  '🦝 Bandit Raccoon',
  '🦜 Parrot Squawk',
  '🐘 Mighty Elephant',
  '🦒 Giraffe Neck',
  '🦓 Striped Zebra',
  '🦘 Jumping Kangaroo',
  '🦙 Llama Drama',
  '🐪 Desert Camel',
  '🦏 Rhino Charge',
  '🦣 Mammoth Power',
  '🐄 Thunder Bull',
  '🐑 Fluffy Sheep',
  '🐐 Mountain Goat',
  '🦌 Antler Deer',
  '🦍 Gorilla Pound',
  '🐵 Cheeky Monkey',
  '🦁 Brave Lion',
  '🐯 Bengal Tiger',
  '🐕 Loyal Dog',
  '🐈 Sleek Cat',
  '🐇 Quick Rabbit',
  '🦝 Clever Raccoon',
  ' Masked Bandit',
  '🦦 Otter Splash',
  '🦡 Badger Bold',
  '🦌 Swift Elk',
  '🦒 Tall Giraffe',
  '🦓 Stripy Zebra',
  '🐘 Elephant Walk',
  '🦏 Rhinoceros Rush',
  '🦛 Hippo Splash',
  '🐢 Tortoise Slow',
  '🦎 Lizard Climb',
  '🦖 T-Rex Terror',
  '🦕 Dino Stomp',
  '🦤 Dodo Echo',
  '🦃 Turkey Trot',
  '🦆 Duck Quack',
  '🦅 Hawk Vision',
  '🦋 Butterfly Float',
  '🐝 Bee Buzz',
  '🦗 Cricket Chirp',
  '🦟 Mosquito Buzz',
  '🦗 Grasshopper Jump',
  '🦂 Scorpion Sting',
  '🕷️ Spider Web',
  '🦑 Squid Ink',
  '🦐 Shrimp Tiny',
  '🦞 Lobster Claws',
  '🦀 Crab Sideways',
] as const;

const AVATAR_SHAPES = [
  '⭕',
  '🔷',
  '🔶',
  '🔹',
  '⬛',
  '⬜',
  '🟨',
  '🟪',
  '🟩',
  '🟥',
] as const;

/**
 * Simple hash function for deterministic user identification
 * Uses DJB2 algorithm - same as userColorUtils
 */
function hashUserId(userId: string): number {
  let hash = 5381;
  for (let i = 0; i < userId.length; i++) {
    hash = ((hash << 5) + hash) + userId.charCodeAt(i);
    hash = hash & hash;
  }
  return Math.abs(hash);
}

/**
 * Get deterministic animal nickname for a user
 * Same userId always returns same nickname
 * Uses combinations of animal names and adjectives to minimize collisions
 */
export function getUserNickname(userId: string): string {
  const hash = hashUserId(userId);
  
  // Use two parts of the hash for better distribution
  const animalIndex = hash % ANIMAL_NICKNAMES.length;
  const variantIndex = Math.floor(hash / ANIMAL_NICKNAMES.length) % 3;
  
  const baseNickname = ANIMAL_NICKNAMES[animalIndex];
  
  // Add Roman numeral variants for better collision avoidance
  if (variantIndex === 0) {
    return baseNickname;
  } else if (variantIndex === 1) {
    return `${baseNickname} II`;
  } else {
    return `${baseNickname} III`;
  }
}

/**
 * Extract just the animal name without emoji
 * "🦊 Fox" -> "Fox"
 * "🦊 Fox II" -> "Fox II"
 */
export function getNicknameText(nickname: string): string {
  return nickname.split(' ').slice(1).join(' ') || 'User';
}

/**
 * Extract emoji from nickname
 * "🦊 Fox" -> "🦊"
 */
export function getNicknameEmoji(nickname: string): string {
  return nickname.split(' ')[0];
}

/**
 * Generate initials from nickname
 * "Fox" -> "FX" or "Fox II" -> "FII" or "Alpha Wolf" -> "AW"
 */
export function getInitials(nickname: string): string {
  const text = getNicknameText(nickname);
  
  // Remove emoji if present
  const cleanText = text.replace(/[\u{1F300}-\u{1F9FF}]/gu, '').trim();
  
  const parts = cleanText.split(' ').filter(p => p.length > 0);
  
  if (parts.length === 1) {
    return parts[0].substring(0, 2).toUpperCase();
  } else if (parts.length >= 2) {
    // Use first letter of first and last part
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }
  
  return 'UN';
}

/**
 * Generate avatar - emoji-based circular avatar
 */
export function generateAvatarEmoji(userId: string): string {
  const hash = hashUserId(userId);
  const index = hash % AVATAR_SHAPES.length;
  return AVATAR_SHAPES[index];
}

/**
 * Generate SVG-based avatar circle with user initials
 * More sophisticated than emoji
 */
export function generateAvatarSVG(
  userId: string,
  backgroundColor: string,
  textColor: string,
  size: number = 40
): string {
  const nickname = getUserNickname(userId);
  const initials = getInitials(nickname);

  const svg = `
    <svg width="${size}" height="${size}" viewBox="0 0 ${size} ${size}" xmlns="http://www.w3.org/2000/svg">
      <circle cx="${size / 2}" cy="${size / 2}" r="${size / 2}" fill="${backgroundColor}" stroke="${textColor}" stroke-width="2"/>
      <text 
        x="${size / 2}" 
        y="${size / 2 + size / 6}" 
        text-anchor="middle" 
        font-size="${size / 2.5}" 
        font-weight="bold" 
        fill="${textColor}"
        font-family="Arial, sans-serif"
      >
        ${initials}
      </text>
    </svg>
  `;

  return `data:image/svg+xml;base64,${btoa(svg)}`;
}

/**
 * Generate complete user identity
 * Returns nickname, avatar, and styling information
 */
export function getUserIdentity(
  userId: string,
  backgroundColor: string,
  theme: Theme = 'light'
): UserIdentity {
  const nickname = getUserNickname(userId);
  const emoji = getNicknameEmoji(nickname);
  const initials = getInitials(nickname);
  
  // Determine text color for contrast
  const textColor = theme === 'light' ? '#000000' : '#FFFFFF';

  return {
    nickname,
    avatar: emoji,
    avatarBg: backgroundColor,
    initials,
  };
}

/**
 * Get color + nickname combo for full user representation
 */
export function getUserProfile(userId: string, userColor: any, theme: Theme = 'light'): UserIdentity & { color: any } {
  const identity = getUserIdentity(userId, userColor.background, theme);
  return {
    ...identity,
    color: userColor,
  };
}

/**
 * Verify nickname consistency (debugging)
 */
export function debugUserIdentity(userId: string) {
  const nickname = getUserNickname(userId);
  const emoji = getNicknameEmoji(nickname);
  const text = getNicknameText(nickname);
  const initials = getInitials(nickname);

  return {
    userId,
    nickname,
    emoji,
    nicknameText: text,
    initials,
  };
}

/**
 * Get all available nicknames (for reference)
 */
export function getAllNicknames() {
  return ANIMAL_NICKNAMES.map(n => ({
    full: n,
    emoji: n.split(' ')[0],
    text: n.split(' ').slice(1).join(' '),
  }));
}
