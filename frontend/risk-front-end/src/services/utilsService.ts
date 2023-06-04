

export function unionArrays(a: any[], b: any[]): any[] {
    const cache: any = {};

    a.forEach(item => cache[item] = item);
    b.forEach(item => cache[item] = item);

    return Object.keys(cache).map(key => cache[key]);
}

export function getAvatar(imageName: string) {
    return `/avatar/${imageName}.png`;
}

export function convertAchievementNameToImagePath(achievementName: string) {
    achievementName = achievementName.replace(/ /g,'');
    achievementName = achievementName.slice(0, 1).toLowerCase() + achievementName.slice(1);
    return `/achievement/${achievementName}.png`;
}