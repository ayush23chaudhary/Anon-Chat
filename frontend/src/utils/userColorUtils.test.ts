/**
 * User Color System - Test & Demo Suite
 * 
 * Run these examples to validate and visualize the color system
 */

import {
  getUserColor,
  getAllUserColors,
  isContrastCompliant,
  debugUserColor,
} from '../utils/userColorUtils';

// ============================================================================
// TEST 1: Basic Deterministic Hashing
// ============================================================================
console.log('=== TEST 1: Deterministic Hashing ===');

const testUserIds = [
  'user-alice',
  'user-bob',
  'user-charlie',
  'user-alice', // Same user as first
];

testUserIds.forEach(userId => {
  const color = getUserColor(userId, 'light');
  console.log(`${userId}: ${color.background}`);
});

// Expected: Same color for 'user-alice' both times
// Expected: Different colors for alice, bob, charlie


// ============================================================================
// TEST 2: Theme Adaptation
// ============================================================================
console.log('\n=== TEST 2: Theme Adaptation ===');

const userId = 'user-demo';
const lightColor = getUserColor(userId, 'light');
const darkColor = getUserColor(userId, 'dark');

console.log(`Light theme: ${lightColor.background}`);
console.log(`Dark theme:  ${darkColor.background}`);

// Expected: Dark theme color is slightly lighter (higher luminance)


// ============================================================================
// TEST 3: WCAG Contrast Compliance
// ============================================================================
console.log('\n=== TEST 3: WCAG AA Contrast Compliance ===');

const allUsers = ['user1', 'user2', 'user3', 'user4', 'user5'];

allUsers.forEach(userId => {
  const lightColor = getUserColor(userId, 'light');
  const darkColor = getUserColor(userId, 'dark');

  const lightCompliant = isContrastCompliant(lightColor, 'light');
  const darkCompliant = isContrastCompliant(darkColor, 'dark');

  console.log(`${userId}: Light=${lightCompliant}, Dark=${darkCompliant}`);
});

// Expected: All return true (all WCAG AA compliant)


// ============================================================================
// TEST 4: All Palette Colors
// ============================================================================
console.log('\n=== TEST 4: Complete Palette (Light Theme) ===');

const lightPalette = getAllUserColors('light');
lightPalette.forEach((color, index) => {
  console.log(`Color ${index + 1}: ${color.background} (text: ${color.text})`);
});

console.log('\n=== Palette (Dark Theme) ===');

const darkPalette = getAllUserColors('dark');
darkPalette.forEach((color, index) => {
  console.log(`Color ${index + 1}: ${color.background} (text: ${color.text})`);
});

// Expected: 12 distinct colors, darker in light theme, lighter in dark theme


// ============================================================================
// TEST 5: Debug Information
// ============================================================================
console.log('\n=== TEST 5: Debug Information ===');

const debugInfo = debugUserColor('user-debug-123', 'light');
console.log('Debug info:', JSON.stringify(debugInfo, null, 2));

// Expected: Complete color info with hue, index, and compliance status


// ============================================================================
// TEST 6: Distinct Color Distribution
// ============================================================================
console.log('\n=== TEST 6: Color Distribution Across Users ===');

// Generate colors for 100 random user IDs
const distributions: Record<string, number> = {};
for (let i = 0; i < 100; i++) {
  const userId = `user-${Math.random().toString(36).slice(2)}`;
  const color = getUserColor(userId, 'light');
  distributions[color.background] = (distributions[color.background] || 0) + 1;
}

console.log('Color usage distribution:');
Object.entries(distributions).forEach(([color, count]) => {
  console.log(`  ${color}: ${count} users`);
});

// Expected: Even distribution across 12 colors (roughly 8-9 users per color)


// ============================================================================
// TEST 7: Text Color Selection
// ============================================================================
console.log('\n=== TEST 7: Automatic Text Color Selection ===');

const textColorTests = [
  'user-light',
  'user-dark',
  'user-contrast',
];

textColorTests.forEach(userId => {
  const lightColor = getUserColor(userId, 'light');
  const darkColor = getUserColor(userId, 'dark');

  console.log(`${userId}:`);
  console.log(`  Light: bg=${lightColor.background}, text=${lightColor.text}`);
  console.log(`  Dark:  bg=${darkColor.background}, text=${darkColor.text}`);
});

// Expected: Text color should be #FFFFFF or #000000 based on background luminance


// ============================================================================
// TEST 8: Consistency Over Time
// ============================================================================
console.log('\n=== TEST 8: Color Consistency ===');

const consistencyUserId = 'consistency-test-user';
const firstCall = getUserColor(consistencyUserId, 'light');
const secondCall = getUserColor(consistencyUserId, 'light');
const thirdCall = getUserColor(consistencyUserId, 'light');

console.log('First call: ', firstCall.background);
console.log('Second call:', secondCall.background);
console.log('Third call: ', thirdCall.background);

const isConsistent =
  firstCall.background === secondCall.background &&
  secondCall.background === thirdCall.background;

console.log(`Consistent: ${isConsistent}`);

// Expected: All three calls return identical colors


// ============================================================================
// VISUAL OUTPUT: HTML Color Swatches
// ============================================================================
export function generateColorSwatchHTML(theme: 'light' | 'dark' = 'light') {
  const colors = getAllUserColors(theme);
  
  let html = `
    <div style="padding: 20px; background: ${theme === 'light' ? '#fff' : '#1a1a1a'};">
      <h2>AnonChat User Color Palette - ${theme} Theme</h2>
      <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); gap: 12px;">
  `;

  colors.forEach((color, index) => {
    html += `
      <div style="
        background: ${color.background};
        border: 2px solid ${color.border};
        color: ${color.text};
        padding: 16px;
        border-radius: 8px;
        text-align: center;
        font-family: monospace;
        font-size: 12px;
      ">
        <div style="font-weight: bold; margin-bottom: 4px;">Color ${index + 1}</div>
        <div>${color.background}</div>
        <div style="font-size: 10px; margin-top: 4px;">Text: ${color.text}</div>
      </div>
    `;
  });

  html += `
      </div>
    </div>
  `;

  return html;
}


// ============================================================================
// EXAMPLE: React Component Usage
// ============================================================================
export function ExampleReactUsage() {
  return `
// In your React component:

import { getUserColor } from '@/utils/userColorUtils';

export function ChatMessage({ userId, message }) {
  const userColor = getUserColor(userId, 'light');

  return (
    <div
      style={{
        backgroundColor: userColor.background,
        border: \`2px solid \${userColor.border}\`,
        color: userColor.text,
        padding: '12px',
        borderRadius: '8px',
      }}
    >
      {message}
    </div>
  );
}
  `;
}


// ============================================================================
// BENCHMARK: Performance Test
// ============================================================================
export function benchmarkColorGeneration() {
  console.log('\n=== PERFORMANCE BENCHMARK ===');

  const iterations = 10000;
  const userIds = Array.from({ length: 100 }, (_, i) => `user-${i}`);

  const start = performance.now();
  for (let i = 0; i < iterations; i++) {
    const userId = userIds[i % userIds.length];
    getUserColor(userId, 'light');
  }
  const end = performance.now();

  const timePerCall = ((end - start) / iterations * 1000).toFixed(2);
  console.log(`${iterations} iterations in ${(end - start).toFixed(2)}ms`);
  console.log(`Average time per call: ${timePerCall}μs`);

  // Expected: < 1ms per call (very fast)
}

benchmarkColorGeneration();
