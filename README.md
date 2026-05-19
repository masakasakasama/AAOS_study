# AAOS Study

Android Automotive OS (AAOS) を**体系的かつ直感的に**学ぶための Android
アプリ。実 HAL には接続せず、AAOS の挙動を忠実に模した**サンドボックス**＋
**3 段階のカリキュラム**で「操作・コード・XML を変えると IVI 画面がどう
変わるか」を体で覚えます。アプリ層（上位レイヤー）を優先。

## 学習コース（初学者 / 中級 / 上級）

| コース | 内容 |
| --- | --- |
| 初学者向け「AAOS をはじめる」 | 全体像 / レイヤー構造 / 最初の Car アプリ / リソース設計 / Polestar 2 ケース |
| 中級向け「Car API を使いこなす」 | CarPropertyManager / プロパティ構造 / HVAC とゾーン / UX 制限 / RRO 徹底解説 / クラスター / 署名と権限 |
| 上級向け「プラットフォームに踏み込む」 | CarService 内部 / VHAL 実装 / AOSP ビルド / 電源・Garage Mode / マルチユーザー・音声 / CTS/VTS・実車フロー |

各レッスンに **Compose 描画の図解**・**実コード**・**AOSP 実ファイルの
対応関係（どのファイルがどこに紐づくか）**・**AOSP デフォルト値**・
**確認クイズ**・**サンドボックスへの導線**を収録。実車の例として
**Polestar 2 / Volvo Cars** を随所で扱います。

## インタラクティブ・サンドボックス

- **VHAL Playground** — 速度/RPM/燃料/ギア/HVAC を動かすと IVI が即変化
- **RRO Theme Lab** — OEM 風の再スキン。生成オーバーレイ XML を表示
- **Car API Explorer** — プロパティ別の実 API と App→Service→VHAL 連鎖
- **Architecture Map** — 各レイヤーをタップで解説
- **Scenario Presets** — 車両＋テーマ状態をワンタップで比較

## 技術スタック

Kotlin / Jetpack Compose / Material 3 / Navigation Compose。状態は単一
ViewModel に集約し、「VHAL 値を変える → IVI が再描画」を実機の
CarPropertyManager コールバックと同じ構図で再現。

## 自動ビルド（アプリの自動更新）

`.github/workflows/android.yml` が `main` および `claude/**` ブランチへの
push で **デバッグ APK を自動ビルド**し、Actions の成果物
（`aaos-study-debug-apk`）として更新します。手動実行も可能
（workflow_dispatch）。

## ローカルビルド

Android Studio で開くか、Android SDK を設定のうえ:

```bash
./gradlew :app:assembleDebug
```

> 注: Gradle Wrapper の jar はリポジトリに含めていません。Android Studio
> で開くか `gradle wrapper` を一度実行してください。CI は Gradle を
> セットアップして自動ビルドします。
