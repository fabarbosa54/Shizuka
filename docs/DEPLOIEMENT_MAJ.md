# 🚀 Guide de diffusion des mises à jour sur GitHub (Releases)

Ce guide explique pas à pas comment publier une nouvelle version officielle de **Shizuka Launcher** sur GitHub afin que :
1. L'APK soit compilé automatiquement par GitHub Actions.
2. Une **GitHub Release** soit créée avec le fichier `Shizuka.apk` téléchargeable.
3. Les utilisateurs et les téléviseurs puissent détecter et installer la mise à jour.

---

## 📋 Résumé du cycle de mise à jour

```
Modifier le code & tester
       ↓
Mettre à jour CHANGELOG.md (avec la nouvelle section ## X.Y.Z)
       ↓
Incrémenter versionCode et versionName dans app/build.gradle.kts
       ↓
Créer un commit et le pousser (git push origin main)
       ↓
Créer un tag git (ex: v1.1.1) et le pousser (git push origin v1.1.1)
       ↓
GitHub Actions compile automatiquement l'APK et publie la Release !
```

---

## 🛠️ Étapes détaillées

### 1. Documenter les nouveautés dans `CHANGELOG.md`
Ouvrez le fichier [`CHANGELOG.md`](file:///d:/scripts/My_Plume_Launcher/CHANGELOG.md) et ajoutez votre nouvelle version tout en haut :

```markdown
## 1.1.1

- Description de la première nouveauté ou correction.
- Autre amélioration visible pour l'utilisateur.
```

> [!IMPORTANT]
> L'application extrait automatiquement les notes de version depuis `CHANGELOG.md` pour l'écran "Nouveautés". Le titre de la section (`## 1.1.1`) **doit correspondre exactement** au `versionName`.

---

### 2. Incrémenter les numéros de version dans `app/build.gradle.kts`
Ouvrez [`app/build.gradle.kts`](file:///d:/scripts/My_Plume_Launcher/app/build.gradle.kts) et mettez à jour le bloc `defaultConfig` :

```kotlin
    defaultConfig {
        applicationId = "dev.plume"
        minSdk = 26
        targetSdk = 36
        versionCode = 3       // ← Incrémenter de 1 à chaque mise à jour (ex: 2 -> 3)
        versionName = "1.1.1" // ← Nouveau numéro de version (ex: "1.1.1")
```

- **`versionCode`** : entier strictement croissant (obligatoire pour qu'Android accepte la mise à jour par-dessus l'ancienne version).
- **`versionName`** : texte visible par l'utilisateur (doit matcher `CHANGELOG.md`).

---

### 3. Valider la compilation locale
Vérifiez que le projet compile bien :

```powershell
.\gradlew.bat assembleDebug
```

---

### 4. Commiter et pousser les modifications sur la branche `main`

```powershell
git add .
git commit -m "chore(release): bump version to 1.1.1"
git push origin main
```

---

### 5. Créer et pousser le Tag Git pour déclencher la Release

C'est cette étape qui lance le flux automatisé [`.github/workflows/release.yml`](file:///d:/scripts/My_Plume_Launcher/.github/workflows/release.yml) :

```powershell
git tag v1.1.1
git push origin v1.1.1
```

*(Remplacez `v1.1.1` par votre tag correspondant, toujours avec le préfixe `v`).*

---

## 🤖 Que se passe-t-il ensuite ?

1. Rendez-vous sur votre dépôt : **https://github.com/riciofab/Shizuka/actions**
2. Vous verrez le workflow **Release** s'exécuter.
3. En ~1 à 2 minutes, il compile le projet et génère le fichier `Shizuka.apk`.
4. Dès la fin du build, la mise à jour est immédiatement disponible dans la section **Releases** :
   👉 **https://github.com/riciofab/Shizuka/releases**

---

## 💡 Astuces utiles

### Comment supprimer un tag si vous vous êtes trompé ?
Si vous avez poussé un mauvais tag ou si vous devez recommencer :

```powershell
# Supprimer le tag localement
git tag -d v1.1.1

# Supprimer le tag sur GitHub
git push origin --delete v1.1.1
```
Vous pouvez ensuite supprimer la Release correspondante directement sur l'interface GitHub si elle a été générée.

