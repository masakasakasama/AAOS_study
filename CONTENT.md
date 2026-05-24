# AAOS Study — 学習コンテンツ全文

Android Automotive OS の学習教材（コース／レッスン／図解の説明／
コード／クイズ／AOSP ファイル対応・デフォルト・標準アセット）の全文。
Kotlin の実データから自動生成。図は Compose 描画のため、ここでは
『図種別＋キャプション』として収録（再現の指針になる）。

## 目次
- 初学者向け: AAOS をはじめる（11 レッスン / 約 120 分）
- 中級向け: Car API を使いこなす（16 レッスン / 約 211 分）
- 上級向け: プラットフォームに踏み込む（10 レッスン / 約 146 分）
- 付録A: Car API カタログ
- 付録B: アーキテクチャ各層
- 付録C: AOSP 標準アセット一覧

---
# 【初学者向け】AAOS をはじめる

> アプリ層を中心に、AAOS の世界観と最初の一歩を体で覚える

レッスン数: 11 / 合計 約 120 分

## モジュール 1: 全体像をつかむ

_AAOS とは何か、車のソフト構造、レイヤー_

## [b1] AAOS とは何か（普通の Android との違い） （約 12 分）

### AAOS = クルマに組み込まれた Android

Android Automotive OS (AAOS) は、スマホの Android と同じAOSP をベースに、車載 IVI（センターディスプレイ）やメーターに最適化したものです。Android Auto（スマホを映すだけ）とは別物で、AAOS は車そのものの OS です。

> 🖼 図: クラスターと IVI は別アプリ・別画面 _(図種別: CLUSTER_VS_IVI)_

> 🖼 図: 全体像の予告：アプリ → … → 車両 の5層 _(図種別: LAYER_STACK)_

### スマホ Android と何が違う？

- 車両データ（速度・燃料・空調…）に触れる Car API がある
- 運転中の操作制限（UX Restrictions）が OS レベルで効く
- OEM がアプリを改修せず見た目を変える RRO 前提の作り
- 起動が一瞬（Suspend/Resume が基本、毎回コールドではない）
- マルチユーザー・マルチディスプレイが標準で重要

> **【ポイント】** 本アプリは実 HAL には接続しません。挙動を忠実に模したサンドボックスで『操作 → IVI 画面の変化』を直感的につかむことを目的にしています。

> **【用語】** IVI = In-Vehicle Infotainment。ナビ/メディア/設定などのセンター画面システム。

**確認クイズ:** Android Auto と AAOS の関係として正しいのは？
- A. 同じもの。呼び方が違うだけ
- B. Android Auto はスマホ投影、AAOS は車載 OS 本体 ✅(正解)
- C. AAOS はスマホ用、Android Auto は車用

解説: Android Auto はスマホ画面を車に映す仕組み。AAOS は車に組み込まれた OS そのもので、ネット接続もアプリも車側。

**▶ 試す: VHAL Playground を触ってみる** — 速度や燃料を動かすと IVI クラスターがどう変わるか、まず体感してから次へ進むと理解が早いです。 `(route: vhal)`


## [b2] クルマのソフト全体像（ECU・CAN・IVI・クラスター） （約 12 分）

### 車は何十個もの小さなコンピュータの集合

エンジン、ブレーキ、ドア、空調…それぞれに ECU（小型の制御コンピュータ）があり、CAN などの車内ネットワークでつながっています。AAOS が動く IVI もこのネットワークの一員で、VHAL を通じて値を読み書きします。

> 🖼 図: AAOS は CAN/Ethernet の先の世界と VHAL で会話 _(図種別: ECU_NETWORK)_

> 🖼 図: 同じ AAOS 上のクラスターとセンター IVI _(図種別: CLUSTER_VS_IVI)_

### 学習者が押さえる構図

- クラスター = 速度計など、安全要件が高い表示専用に近い画面
- IVI = アプリが載るリッチな画面（あなたの主戦場）
- 両者は別ディスプレイ・別アプリだが同じ AAOS 上で動く

> **実例: Volvo / Polestar**
>
> Polestar 2（2020年）は Google ビルトインの AAOS を量産車で世界初採用。Volvo Cars はクラスターも AAOS 化し、ナビをメーター内に出すなど IVI とクラスターを統合 UX にしています。『1 つの OS で 2 画面』の好例です。

**確認クイズ:** クラスター画面の特徴として最も適切なのは？
- A. 自由にサードパーティアプリを載せる場所
- B. 速度計など安全性要件が高い表示が中心 ✅(正解)
- C. 実は IVI と同じ 1 枚の画面

解説: クラスターは走行に直結する情報表示が中心で要件が高い。アプリの主戦場はリッチな IVI 側です。


## [b3] AAOS のレイヤー構造（アプリ層が主戦場） （約 14 分）

### 上から下へ 5 層

> 🖼 図: アプリ → Car API → CarService → VHAL → 車両 _(図種別: LAYER_STACK)_

OEM 開発の体感の 9 割は最上段『アプリ層』です。実機の特別ビルドが無くても、Car API の使い方・リソース設計・UX 制限対応の学習はここで完結します。

**ファイル対応: 各層が AOSP のどこにあるか**

| from | → | to | 補足 |
|---|---|---|---|
| `あなたのアプリ (app/src/...)` | → | `android.car.* （Car API ライブラリ）` | アプリは Car API を import して使う。実体は次行。 |
| `android.car.Car / CarPropertyManager` | → | `packages/services/Car/car-lib/src/android/car/` | クライアント側 SDK。Binder で CarService を呼ぶ。 |
| `CarService (CarPropertyService 等)` | → | `packages/services/Car/service/src/com/android/car/` | permission・subscribe・UX 制限を司る常駐システムサービス。 |
| `VehiclePropertyIds 契約` | → | `hardware/interfaces/automotive/vehicle/ (AIDL)` | Android と車両の安定インターフェース定義。 |

> **【ポイント】** 『どのファイルがどこに紐づくか』はこのコース全体で繰り返し出てきます。最初は層の名前と場所だけ覚えれば OK。

**▶ 試す: Architecture Map で層をタップ** — 各層を開いて、役割と代表クラスを確認しましょう。 `(route: arch)`

**確認クイズ:** OEM のアプリ開発で最も時間を使う層は？
- A. VHAL
- B. アプリ層 ✅(正解)
- C. Linux カーネル

解説: アプリ層が主戦場。だからこのコースもアプリ層を優先します。


## モジュール 2: 最初のコードと作法

_Car API の入口、リソース設計の考え方_

## [b4] はじめての Car アプリ（Car.createCar） （約 13 分）

### Car API の入口は必ず Car オブジェクト

車両データに触るには、まず Car インスタンスを作り、そこから目的の Manager を取り出します。使い終わったら必ず disconnect します。

```kotlin
val car = Car.createCar(context)
val props = car.getCarManager(Car.PROPERTY_SERVICE)
        as CarPropertyManager
// ... 使う ...
car.disconnect()
```

> 🖼 図: アプリ → Manager → CarService → VHAL → 車両 _(図種別: DATA_FLOW)_

> 🖼 図: set/get と subscribeの往復 _(図種別: API_SEQUENCE)_

**ファイル対応: この 1 行が何に紐づくか**

| from | → | to | 補足 |
|---|---|---|---|
| `Car.createCar(context)` | → | `packages/services/Car/car-lib/.../Car.java` | ここで CarService へ bind する。 |
| `Car.PROPERTY_SERVICE 文字列キー` | → | `CarPropertyManager（同 car-lib 内）` | キーに対応する Manager 実体が返る。 |

> **【注意】** AndroidManifest で <uses-library android:name="android.car"/> が必要。さらに書込み系は車両 permissionが要る（中級で詳説）。

**▶ 試す: Car API Explorer を見る** — 接続・速度subscribe・HVAC 設定など、実コードと呼び出し連鎖をプロパティ別に確認できます。 `(route: api)`

**確認クイズ:** Car API を使い終わったら必ず何をする？
- A. car.finish()
- B. car.disconnect() ✅(正解)
- C. 何もしなくてよい

解説: disconnect() で CarService への接続を解放する。Activity の onDestroy 等で呼ぶのが定石。


## [b5] リソースと XML の基礎（なぜ色を直書きしないか） （約 13 分）

### 色・文字・寸法はコードに埋め込まない

> 🖼 図: リソース名そのまま、値だけ overlay で差し替え _(図種別: RRO_OVERLAY)_

> 🖼 図: 同名リソースがファイル単位でどう紐づくか _(図種別: RRO_FILEMAP)_

AAOS では OEM がアプリを再コンパイルせずに見た目だけ差し替えます。だから色や文字列はリソース名で参照し、実値は XML 側に置きます。これが RRO（次レッスン）の前提。

```xml
<!-- res/values/colors.xml -->
<resources>
    <color name="cluster_accent">#1A73E8</color>
</resources>
```

```kotlin
// 直書き ✗
val c = Color(0xFF1A73E8)
// リソース参照 ○（OEM が上書き可能）
val c = colorResource(R.color.cluster_accent)
```

**AOSP デフォルト: AOSP のリソース既定**

| 項目 | デフォルト値 | 定義場所 |
|---|---|---|
| アプリ既定テーマ/色 | AOSP リファレンス値（OEM 無指定時に使われる） | `各アプリの res/values/ + frameworks/base/core/res/` |
| framework 全体の設定既定 | config_* キーの初期値 | `frameworks/base/core/res/res/values/config.xml` |

**▶ 試す: RRO Theme Lab で色を変える** — リソース名はそのまま、値だけ変えると同じ UI がどう変わるかを体感してください。 `(route: rro)`

**確認クイズ:** 色をコードに直書きするとなぜ困る？
- A. コンパイルが遅くなる
- B. OEM が再コンパイルせずに見た目を変えられない ✅(正解)
- C. 色が表示されない

解説: RRO はリソースを実行時に差し替える仕組み。直書きは差し替え対象にならず、OEM がブランド化できない。


## モジュール 3: 実例と総まとめ

_Polestar 2 ケース、用語整理_

## [b6] ケーススタディ：Polestar 2（世界初の量産 AAOS） （約 11 分）

### 実車で AAOS はどう使われたか

> 🖼 図: Polestar/Volvo はクラスターと IVI を1つの OS で統合 _(図種別: CLUSTER_VS_IVI)_

> 🖼 図: ブランド色/ロゴは RRO で差し替え _(図種別: RRO_OVERLAY)_

> **実例: Polestar 2 / Volvo Cars**
>
> Polestar 2 は 2020 年、Google ビルトイン（Google マップ・アシスタント・Play）を載せた AAOS を量産車で世界初採用。Volvo はその後ライン全体へ展開し、メーター内ナビなどクラスターと IVI を 1 つの OS で統合しました。

学習者の観点で重要なのは『OEM はアプリを書き換えず、RRO とプロパティ設定でブランド体験を作っている』点。あなたが書くアプリも同じ作法に従えば OEM に載ります。

- ブランド色/ロゴ/アイコン → RRO で差し替え
- 速度・電費・空調 → VHAL プロパティで取得
- 走行中の制限 → CarUxRestrictions に従う

**▶ 試す: Scenario Presets で雰囲気を比較** — 『Nordic minimal』など、北欧的なクラスター表現をワンタップで再現して見比べられます。 `(route: scenarios)`

**確認クイズ:** Polestar 2 が AAOS 史で特筆される理由は？
- A. 最初の Android Auto 対応車
- B. Google ビルトイン AAOS を量産車で世界初採用 ✅(正解)
- C. AAOS を使わない独自 OS だった

解説: スマホ投影の Android Auto ではなく、車載 OS 本体としての AAOS + Google サービスを量産で初めて載せた。


## [b7] 用語ミニ辞典 + 初学者総まとめ （約 9 分）

### ここまでの用語を一気に整理

> 🖼 図: 復習：5層と用語の位置関係 _(図種別: LAYER_STACK)_

> 🖼 図: 復習：値が流れる道筋 _(図種別: DATA_FLOW)_

> **【用語】** VHAL: 車両と Android の安定インターフェース。

> **【用語】** CarPropertyManager: アプリ側から車両値を読み書きする窓口。

> **【用語】** RRO: 実行時にリソースを差し替える OEM 向け仕組み。

> **【用語】** Cluster: 速度計などのメーター画面。

> **【用語】** UX Restrictions: 走行中の操作制限ポリシー。

中級では CarPropertyManager と RRO を実装レベルで掘ります。

**確認クイズ:** 次のうち『アプリ側の窓口クラス』は？
- A. VHAL
- B. CarPropertyManager ✅(正解)
- C. CAN バス

解説: VHAL は契約、CAN は物理ネットワーク。アプリが触るのはCarPropertyManager。


## モジュール 4: 実機への橋渡し

_エミュレータ起動と Auto/AAOS の整理_

## [b8] AAOS エミュレータを入手して起動する （約 12 分）

### 実機が無くても学べる

> 🖼 図: ソース → ビルド → エミュレータ → adb install _(図種別: BUILD_PIPELINE)_

Android Studio の Device Manager で『Automotive』系のシステムイメージを選ぶと、AAOS エミュレータが使えます。本アプリで概念をつかみ、エミュレータで実物を触る、の二段構えが最短ルートです。

- Android Studio → Device Manager → Create device
- Category で Automotive を選択
- Google APIs / Play 付きイメージを選ぶ
- 起動後 `adb devices` で認識を確認

**▶ 試す: ADB ブリッジを見る** — 本アプリの状態を、起動したエミュレータへ流し込むコマンドが自動生成されます。 `(route: adb)`

**確認クイズ:** AAOS エミュレータを作るとき選ぶカテゴリは？
- A. Phone
- B. Automotive ✅(正解)
- C. TV

解説: Automotive カテゴリの car 系イメージが AAOS。


## [b9] Android Auto と AAOS の境界を一枚で （約 9 分）

### 混同しやすい 2 つを整理

> 🖼 図: 投影 (Auto) と OS 本体 (AAOS) _(図種別: CLUSTER_VS_IVI)_

- Android Auto: スマホ画面を車に投影。アプリはスマホ側
- AAOS: 車に組込んだ OS。アプリは車側で動く
- 学習対象は AAOS（このアプリも AAOS を扱う）

**確認クイズ:** アプリが車側で動くのはどっち？
- A. Android Auto
- B. AAOS ✅(正解)
- C. どちらも同じ

解説: AAOS は OS 本体。Auto は投影でアプリはスマホ側。


## モジュール 5: AOSP の土台を知る

_標準で付いてくる参照アプリと再利用_

## [b11] AOSP には『土台アプリ』が標準で付いてくる （約 8 分）

### ゼロから作らない。標準アセットを使う

> 🖼 図: アプリ / リソース / API・基盤 の3層 _(図種別: ASSET_MAP)_

AAOS には設定・メディア・電話・SystemUI などの参照アプリと、Car API/基盤が標準で付いてきます。OEM はこれを土台に、RRO で見た目を変え、足りない所だけ作ります。

**AOSP 標準アセット: 代表的な参照アプリ**

| アプリ/素材 | 再利用度 | 役割 | 場所 |
|---|---|---|---|
| Car SystemUI | ◎ 土台に最重要 | system bars / 通知 / 音量 / ユーザー切替 /(HVACパネル) | `packages/apps/Car/SystemUI` |
| Car Settings | ◎ 項目追加が主作業 | 設定アプリ（項目が豊富） | `packages/apps/Car/Settings` |
| Car Media | ★ ほぼそのまま | メディアセンター（MediaBrowserService を束ねる） | `packages/apps/Car/Media` |
| Car Launcher | ○ 独自置換も多い | ホーム / アプリグリッド / 地図・メディアカード | `packages/apps/Car/Launcher` |
| Car Dialer | ◎〜○ | 電話 / 連絡先（Bluetooth HFP） | `packages/apps/Car/Dialer` |

> **【ポイント】** 『再利用度』＝そのまま使える(★)〜RROだけ(◎)〜作り込み(△)。中級コースで全体像と使い分けを詳しく扱います。

**確認クイズ:** AOSP 参照アプリの一番うれしい点は？
- A. 毎回自作が必要
- B. 土台として再利用でき、差分だけ作れる ✅(正解)
- C. 改変できない

解説: 標準アセットを土台に差分開発できるのが最大の利点。


## モジュール 6: このアプリの裏側

_自動更新の仕組み（学んだ流れの実例）_

## [b10] このアプリ自体の自動更新の仕組み （約 7 分）

### 学んだ流れが、このアプリの更新にも使われている

> 🖼 図: push → CI build → release → アプリが確認 → 更新 _(図種別: UPDATE_FLOW)_

このアプリは Play Store 外の sideload（手動配布）なので、GitHub Actions が push のたびに APK を build し、rolling release に添付します。アプリは起動時に最新の commit SHA を比較し、新しければダイアログを出します。

- ファイル名は常に app-debug.apk（中身だけ差し替え）
- 見分けはホーム最下部の『ビルド 0.1.N+sha』表示で
- 実インストールは Android 仕様でユーザー確認が必須

> **【ポイント】** 完全無確認の自動更新は OS が許可しないため、『自動チェック＋ワンタップ更新』が上限です。

**確認クイズ:** sideload アプリの更新で最後に必ず必要なのは？
- A. 何も不要で自動完了
- B. ユーザーのインストール確認 ✅(正解)
- C. Play Store 審査

解説: 提供元不明のアプリ許可とインストール確認は OS 必須。

---
# 【中級向け】Car API を使いこなす

> プロパティ・HVAC・UX 制限・RRO・クラスターを実装視点で

レッスン数: 16 / 合計 約 211 分

## モジュール 1: プロパティ実装

_read/write/subscribe とプロパティ構造、HVAC_

## [i1] CarPropertyManager 詳細（read / write / subscribe） （約 16 分）

### 3 つのアクセス様式

> 🖼 図: set/get と subscribeの流れ _(図種別: API_SEQUENCE)_

- 単発 read: getIntProperty / getFloatProperty
- 単発 write: setIntProperty / setFloatProperty
- subscribe: registerCallback（連続センサーはこれ）

```kotlin
propertyManager.registerCallback(
    object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(v: CarPropertyValue<*>) {
            val mps = v.value as Float
            updateSpeedo(mps * 3.6f)   // m/s -> km/h
        }
        override fun onErrorEvent(p: Int, a: Int) {}
    },
    VehiclePropertyIds.PERF_VEHICLE_SPEED,
    CarPropertyManager.SENSOR_RATE_UI,
)
```

> 🖼 図: subscribeの値は逆向きに callback で返る _(図種別: DATA_FLOW)_

**ファイル対応: API がどこに紐づくか**

| from | → | to | 補足 |
|---|---|---|---|
| `CarPropertyManager.registerCallback()` | → | `packages/services/Car/service/.../CarPropertyService.java` | Binder 越しにsubscribe登録され、Service が VHAL をsubscribe。 |
| `VehiclePropertyIds.PERF_VEHICLE_SPEED` | → | `hardware/interfaces/automotive/vehicle/.../VehicleProperty` | プロパティ ID の正体は VHAL 契約の enum。 |

> **【注意】** 連続値をポーリングしない。必ずsubscribeし、変化時だけ処理する。

**▶ 試す: VHAL Playground** — 速度スライダーを動かし、subscribe相当でメーターが追従する様子を確認しましょう。 `(route: vhal)`

**確認クイズ:** 連続的に変わる速度を扱う最適解は？
- A. ループで getFloatProperty
- B. registerCallback でsubscribe ✅(正解)
- C. setProperty

解説: 連続センサーはsubscribe。ポーリングは無駄と遅延の元。


## [i2] プロパティの解剖（id / area / access / changeMode） （約 14 分）

### 1 つのプロパティは 4 つの属性を持つ

> 🖼 図: id を軸に area/access/changeMode/型 _(図種別: PROPERTY_ANATOMY)_

> 🖼 図: areaId = どの“場所”の値か _(図種別: AREA_ID)_

> 🖼 図: changeMode で subscribe 時の出方が変わる _(図種別: CHANGE_MODE)_

- area: GLOBAL か、座席/窓などゾーン別か（areaId）
- access: READ / WRITE / READ_WRITE
- changeMode: STATIC / ONCHANGE / CONTINUOUS
- 型: Int / Float / Int[] など

```kotlin
val zone = VehicleAreaSeat.SEAT_ROW_1_LEFT
propertyManager.setFloatProperty(
    VehiclePropertyIds.HVAC_TEMPERATURE_SET, zone, 22.0f)
```

**AOSP デフォルト: 参照 VHAL のデフォルト値はどこ？**

| 項目 | デフォルト値 | 定義場所 |
|---|---|---|
| 各プロパティの初期値・対応エリア | リファレンス実装の既定値（速度0、ギアP 等） | `hardware/interfaces/automotive/vehicle/aidl/impl/default_config/ (DefaultProperties.json 等)` |
| 旧 HIDL 実装の既定 | DefaultConfig.h に C++ で定義 | `hardware/interfaces/automotive/vehicle/2.0/default/` |

**確認クイズ:** 座席ごとに温度が違う HVAC は何が複数ある？
- A. propertyId
- B. areaId ✅(正解)
- C. changeMode

解説: ゾーン別プロパティは 1 つの id に複数の areaId を持つ。


## [i3] HVAC 制御とゾーン・permission （約 14 分）

### 空調はゾーン × permissionの典型例

> 🖼 図: areaId(VehicleAreaSeat) で座席を指定 _(図種別: HVAC_ZONES)_

> 🖼 図: 書込みは CarService がpermissionを検査 _(図種別: PERMISSION_FLOW)_

HVAC 書込みには Car.PERMISSION_CONTROL_CAR_CLIMATE が必要。permissionが無い・signingが合わないと SecurityException。

**ファイル対応: permissionはどこで定義/検査されるか**

| from | → | to | 補足 |
|---|---|---|---|
| `Car.PERMISSION_CONTROL_CAR_CLIMATE` | → | `packages/services/Car/car-lib/.../Car.java` | permission文字列の定義場所。 |
| `permissionの宣言（protectionLevel）` | → | `packages/services/Car/service/AndroidManifest.xml` | signature|privileged 等の保護レベルがここ。 |

**▶ 試す: VHAL Playground で HVAC を操作** — 設定温度・ファン段を変えると IVI の HVAC 表示が即変化。 `(route: vhal)`

**確認クイズ:** HVAC 書込みで SecurityException が出る主因は？
- A. 値が範囲外
- B. 車両 permission/signingの不足 ✅(正解)
- C. areaId が 0

解説: 制御系プロパティは保護レベルが高く、permissionとsigningが要る。


## モジュール 2: OEM 作法

_UX 制限と RRO の徹底理解_

## [i4] UX Restrictions（走行中の操作制限） （約 13 分）

### 走行中は OS が UI を制限する

> 🖼 図: 停車中フル UI ↔ 走行中の制限 UI _(図種別: UXR_STATE)_

CarUxRestrictionsManager を subscribeし、走行中は文字量・キーボード・動画などを抑制します。これは法規/安全要件であり、OEM 審査で必ず見られます。

```kotlin
val uxr = car.getCarManager(Car.CAR_UX_RESTRICTION_SERVICE)
        as CarUxRestrictionsManager
uxr.registerListener { r ->
    val limited = r.isRequiresDistractionOptimization
    setUiToDriveSafe(limited)
}
```

**AOSP デフォルト: UXR の既定ポリシー**

| 項目 | デフォルト値 | 定義場所 |
|---|---|---|
| 走行中に許す UI の度合い | 速度等の条件付き既定（OEM が上書き可） | `packages/services/Car/service/res/xml/car_ux_restrictions_map.xml` |

> **実例: Volvo / Polestar**
>
> 走行中はリスト項目数や入力 UI を制限し、停車時に解放する設計。アプリは制限フラグに従うだけで OEM 横断で整合。

**確認クイズ:** 走行中 UI 制限の根拠は？
- A. 見た目の好み
- B. 安全/法規要件 ✅(正解)
- C. 電池節約

解説: ドライバー気そらし防止の安全・法規要件。必須対応。


## [i5] RRO 徹底解説（OverlayManager / target / overlay） （約 16 分）

### OEM はアプリを再ビルドしない

> 🖼 図: base APK + overlay APK → 解決 → 画面 _(図種別: RRO_OVERLAY)_

> 🖼 図: 同名リソースがファイル単位でどう紐づくか _(図種別: RRO_FILEMAP)_

OEM は対象パッケージのリソース名を別 APK（オーバーレイ）で再定義し、OverlayManager で有効化します。アプリのバイトコードは無改修のまま見た目が変わります。

```xml
<!-- overlay APK 側 AndroidManifest.xml -->
<overlay android:targetPackage="com.android.car.settings"
         android:targetName="CarSettings"
         android:isStatic="true" android:priority="1"/>
```

**ファイル対応: RRO のファイル対応**

| from | → | to | 補足 |
|---|---|---|---|
| `システムアプリの @color/... 参照` | → | `OEM オーバーレイ APK の res/values/*.xml 同名定義` | 名前が一致した値が実行時に優先される。 |
| `オーバーレイ APK の置き場所` | → | `device/<oem>/.../overlay/ または vendor/.../overlay/` | ビルドに同梱され OverlayManager が認識。 |
| `有効化を司る本体` | → | `frameworks/base/services/core/.../om/ (OverlayManager)` | どのオーバーレイを優先するか解決する。 |

**AOSP デフォルト: RRO 無効時（= AOSP デフォルト）**

| 項目 | デフォルト値 | 定義場所 |
|---|---|---|
| 色/ロゴ/寸法 | システムアプリ同梱の AOSP リファレンス値 | `packages/apps/Car/<App>/res/values/` |

**▶ 試す: RRO Theme Lab** — 色・角丸・夜間モードを変えると生成オーバーレイ XML が更新されます。OEM が書くファイルそのものです。 `(route: rro)`

**確認クイズ:** RRO で見た目が変わってもアプリ側は？
- A. 再コンパイルが必要
- B. コード/バイトコードは無改修 ✅(正解)
- C. 起動不能

解説: リソース解決が差し替わるだけ。コードは触らない。


## モジュール 3: クラスターと配備

_クラスター実装、signingとpermissionの現実_

## [i6] インストルメントクラスターアプリの作り方 （約 15 分）

### クラスターは『安全な表示』が主役

> 🖼 図: クラスターは要件が高い別アプリ _(図種別: CLUSTER_VS_IVI)_

クラスターは速度・ギア・警告灯などをsubscribeして描画。AOSP には参照クラスターアプリがあり、これを土台に OEM がRRO で意匠を当てます。

**ファイル対応: クラスター関連のファイル対応**

| from | → | to | 補足 |
|---|---|---|---|
| `参照クラスター/インスト群` | → | `packages/apps/Car/ 配下（Cluster 系アプリ）` | OEM はこれを RRO でブランド化、または置換。 |
| `クラスター表示の枠組み` | → | `android.car.cluster.* (car-lib)` | ナビ等の情報をクラスター面へ渡す API。 |

**▶ 試す: Scenario でクラスター表現を比較** — Highway / Nordic / Classic を切替えて意匠差を体感。 `(route: scenarios)`

**確認クイズ:** クラスターアプリの設計で最優先は？
- A. 派手なアニメ
- B. 安全に関わる情報の確実な表示 ✅(正解)
- C. 広告枠

解説: 走行安全に直結。確実・低遅延・規制準拠が最優先。


## [i7] signing・システムアプリ・permissionの現実 （約 12 分）

### なぜ普通の APK では動かない API があるか

制御系プロパティや一部 Manager は signature|privilegedpermissionが必要。OEM のプラットフォーム鍵でsigningし、privapp-permissions allowlist に載って初めて使えます。

**ファイル対応: permission許可リストの対応**

| from | → | to | 補足 |
|---|---|---|---|
| `アプリが要求する車両 permission` | → | `etc/permissions/privapp-permissions-*.xml` | privileged アプリの許可ホワイトリスト。 |
| `プラットフォームsigning鍵` | → | `build/make/target/product/security/ (AOSP 既定鍵)` | 実 OEM は独自鍵に差し替える。 |

> **【注意】** 学習目的のサンドボックス（本アプリ）はpermission不要。実機の制御系を触るにはsigning・allowlist が前提と理解しておく。

**確認クイズ:** 制御系 Car API を一般 APK で呼ぶと？
- A. 普通に動く
- B. SecurityException 等で弾かれる ✅(正解)
- C. 警告だけ

解説: signature|privileged permissionと allowlist が無いと拒否される。


## モジュール 4: 運用の勘所

_subscribeライフサイクル、SystemUI と配置_

## [i8] subscribeのライフサイクルとスレッド （約 13 分）

### 登録したら必ず解除する

registerCallback は onStart、unregisterCallback は onStop。コールバックはメインスレッドとは限らないため、UI 更新はメインへ戻す。Compose なら State 更新→自動再描画が楽。

```kotlin
override fun onStart() {
    propertyManager.registerCallback(cb,
        VehiclePropertyIds.PERF_VEHICLE_SPEED,
        CarPropertyManager.SENSOR_RATE_UI)
}
override fun onStop() {
    propertyManager.unregisterCallback(cb)
}
```

> **【注意】** 解除漏れはリークと無駄な電力消費。ライフサイクルに紐付ける。

**確認クイズ:** unregisterCallback を呼ぶ典型箇所は？
- A. onCreate
- B. onStop / onDestroy ✅(正解)
- C. コンストラクタ

解説: 登録は表示開始、解除は表示終了に対で行う。


## [i9] SystemUI / システムバーと XML 配置 （約 14 分）

### ナビバー/ステータスバーも『アプリ＋リソース』

AAOS の SystemUI は車向けで、バーの有無・位置・高さはリソースとレイアウトで決まります。OEM は RRO で寸法や表示要素を差し替え、画面構成を変えます。

**ファイル対応: SystemUI のファイル対応**

| from | → | to | 補足 |
|---|---|---|---|
| `Car SystemUI 本体` | → | `packages/apps/Car/SystemUI/ (frameworks 由来を継承)` | ナビバー/ステータスバーのレイアウトとロジック。 |
| `バー高さ等の寸法` | → | `同 res/values/dimens.xml（RRO で上書き対象）` | OEM はここを overlay して配置を変える。 |

**AOSP デフォルト: システムバーの既定**

| 項目 | デフォルト値 | 定義場所 |
|---|---|---|
| ナビ/ステータスバーの有無・高さ | Car SystemUI のリファレンス寸法 | `packages/apps/Car/SystemUI/res/values/` |

**確認クイズ:** OEM がバー配置を変える主手段は？
- A. SystemUI を fork して再ビルド必須
- B. RRO で寸法/構成を上書き ✅(正解)
- C. 不可能

解説: 多くは RRO による寸法・構成の差し替えで対応する。


## モジュール 5: AOSP 標準アセット活用

_参照アプリを土台に、再利用度で使い分ける_

## [i10] AOSP 標準アセットを土台にする（参照アプリ活用） （約 16 分）

### 大方針：作らない。土台を使い、足りない所だけ

> 🖼 図: アプリ / リソース / API・基盤 の3層と再利用度 _(図種別: ASSET_MAP)_

AAOS は『参照アプリ＋標準リソース＋ Car API/基盤』がセットで来ます。OEM の基本戦略は、これらを土台にしてRRO で見た目を差し替え、足りない機能だけ作ること。ゼロから書かないのがコスト最小です。

### 参照アプリ（再利用度つき）

**AOSP 標準アセット: packages/apps/Car/* と関連**

| アプリ/素材 | 再利用度 | 役割 | 場所 |
|---|---|---|---|
| Car SystemUI | ◎ 土台に最重要 | system bars / 通知 / 音量 / ユーザー切替 /(HVACパネル) | `packages/apps/Car/SystemUI` |
| Car Settings | ◎ 項目追加が主作業 | 設定アプリ（項目が豊富） | `packages/apps/Car/Settings` |
| Car Media | ★ ほぼそのまま | メディアセンター（MediaBrowserService を束ねる） | `packages/apps/Car/Media` |
| Car Launcher | ○ 独自置換も多い | ホーム / アプリグリッド / 地図・メディアカード | `packages/apps/Car/Launcher` |
| Car Dialer | ◎〜○ | 電話 / 連絡先（Bluetooth HFP） | `packages/apps/Car/Dialer` |
| Car Messenger | ○ 部分改造 | 通知読み上げ / 定型返信（SMS・MAP） | `packages/apps/Car/Messenger` |
| Car Radio | ○ HW 依存 | 放送ラジオ（BroadcastRadio HAL 依存） | `packages/apps/Car/Radio (※版差)` |
| Cluster 系 | △ 雛形・作り込み前提 | メーター描画（DirectRenderingCluster 等） | `packages/apps/Car/Cluster* (※版で大変動)` |
| CarDeveloperOptions | ★ そのまま | 開発者向け設定 | `packages/apps/Car/DeveloperOptions (※)` |
| EmbeddedKitchenSinkApp | ☆ 学習に最強・製品外 | Car API 総当たりサンプル / テスト | `packages/services/Car/tests/EmbeddedKitchenSinkApp` |

### API/基盤層（基本そのまま使う）

**AOSP 標準アセット: android.car / CarService / 参照 VHAL**

| アプリ/素材 | 再利用度 | 役割 | 場所 |
|---|---|---|---|
| android.car (各 Manager) | ★ そのまま使う | CarPropertyManager / CarUxRestrictionsManager / CarAudioManager 他 | `packages/services/Car/car-lib` |
| CarService | ★ プラットフォーム提供 | permission・subscribe 集約・dispatch・UX restriction | `packages/services/Car/service` |
| 参照 VHAL（default config 付き） | ◎ 値・対応を差し替え | プロパティの既定値・get/set/subscribe の参照実装 | `hardware/interfaces/automotive/vehicle/aidl/impl` |

- ★ そのまま: Media / DeveloperOptions / android.car / CarService
- ◎ RRO で足りる: SystemUI / Settings / Dialer / 参照 VHAL
- ○ 部分改造: Launcher / Messenger / Radio
- △ 雛形(作り込み前提): Cluster
- ☆ 学習・テスト用(製品外): EmbeddedKitchenSinkApp

> **【ポイント】** パッケージ名・場所は AOSP バージョンで変わります（特に Cluster と HVAC は版差が大きい）。表の場所は目安です。

> **実例: Volvo / Polestar**
>
> 共通の AAOS 参照アプリを土台に、車種差は RRO・VHAL config・audio 構成へ寄せる運用。安定 API に寄せるほど新車種の立ち上げが速い、という再利用の好例。

**▶ 試す: RRO テーマラボで『差し替え』を体感** — 参照アプリの見た目を、コード無改修で RRO だけ変えるイメージをラボで掴めます。 `(route: rro)`

**確認クイズ:** OEM が最初に取る基本戦略は？
- A. 全アプリをゼロから自作
- B. AOSP 参照アプリを土台に RRO で差し替え＋不足分だけ作る ✅(正解)
- C. AOSP を使わず独自 OS

解説: 土台（参照アプリ＋基盤）を再利用し、差分だけ作る/RRO するのが量産で最もスケールする。


## モジュール 6: 参照アプリ深掘り

_主要な参照アプリを個別に。何が来て、どこを RRO/作り込みするか_

## [d1] 深掘り: Car SystemUI（土台に最重要） （約 13 分）

### 画面の骨格そのもの

> 🖼 図: Status Bar / アプリ領域 / Nav Bar _(図種別: SYSTEMUI_BARS)_

Car SystemUI は status bar・nav bar・通知・音量・ユーザー切替、版によっては HVAC パネルまで担う『画面の枠』。OEM はこれを土台に、RRO で寸法・要素・色を差し替えます。

**ファイル対応: Car SystemUI のファイル対応**

| from | → | to | 補足 |
|---|---|---|---|
| `本体（bar/通知/音量のロジックとレイアウト）` | → | `packages/apps/Car/SystemUI` | frameworks の SystemUI を車向けに拡張。 |
| `バー高さ・表示要素の寸法/真偽値` | → | `同 res/values/（dimens.xml / config）` | RRO の主な overlay 対象。 |

- RRO で変える: バー高さ / 表示要素 / 色 / アイコン
- OEM が足す: 独自クイック設定 / ブランド時計など
- 再利用度: ◎（骨格はそのまま使うのが定石）

**確認クイズ:** OEM がナビバーの高さを変える主手段は？
- A. SystemUI を fork して再ビルド
- B. RRO で dimens を overlay ✅(正解)
- C. 不可能

解説: 寸法は dimens としてリソース化されており RRO で差し替える。


## [d2] 深掘り: Car Settings（項目を足す） （約 11 分）

### 設定の参照実装。項目追加が主作業

Car Settings は Wi-Fi・Bluetooth・表示・ユーザーなど豊富な設定画面を提供。OEM は項目の追加/削除と意匠の差し替えが中心で、ゼロから作ることは稀です。

**ファイル対応: Car Settings のファイル対応**

| from | → | to | 補足 |
|---|---|---|---|
| `設定アプリ本体（各 Preference 画面）` | → | `packages/apps/Car/Settings` | Preference 階層と表示ロジック。 |
| `設定項目の構成 XML` | → | `同 res/xml/（preference 階層）` | 項目の出し分けは RRO/設定で調整。 |

- RRO で変える: 表示項目の有無 / 文言 / 色
- OEM が足す: 車種固有設定（ドライブモード等）
- 再利用度: ◎

**確認クイズ:** Car Settings に対する OEM の典型作業は？
- A. 全画面を自作
- B. 項目の追加/削除と意匠差し替え ✅(正解)
- C. 使わず独自設定アプリ必須

解説: 豊富な参照実装を土台に、差分（項目・意匠）を当てる。


## [d3] 深掘り: Car Media（ほぼそのまま） （約 12 分）

### 自分で書かず、3rd party を束ねる枠

> 🖼 図: MediaBrowserService を実装したアプリが載る _(図種別: MEDIA_AGG)_

Car Media は共通 UI で複数のメディアアプリを横断します。各メディアアプリは MediaBrowserService を実装するだけでこの枠に載るため、OEM/開発者の改変は最小です。

**ファイル対応: Car Media のファイル対応**

| from | → | to | 補足 |
|---|---|---|---|
| `メディアセンター UI 本体` | → | `packages/apps/Car/Media` | MediaBrowser/MediaSession を共通 UI で表示。 |
| `あなたのメディアアプリ側` | → | `MediaBrowserServiceCompat を実装` | これだけで Car Media に載る（個別 UI 不要）。 |

- RRO で変える: 色・アイコン程度
- 開発者が足す: 自分のアプリに MediaBrowserService
- 再利用度: ★（ほぼそのまま）

**確認クイズ:** 自作の音楽アプリを Car Media に載せるには？
- A. Car Media を改造
- B. MediaBrowserService を実装する ✅(正解)
- C. RRO を書く

解説: 共通 UI 側は触らず、アプリが MediaBrowserService を出すだけ。


## [d4] 深掘り: Car Launcher（置換も多い） （約 10 分）

### ホーム。RRO か、独自置換か

Car Launcher はアプリグリッドに加え、地図やメディアのカードを並べる『起点』。RRO で足りる場合もあれば、OEM が独自ランチャーに置換する場合も多い領域です。

**ファイル対応: Car Launcher のファイル対応**

| from | → | to | 補足 |
|---|---|---|---|
| `ランチャー本体（カード/グリッド）` | → | `packages/apps/Car/Launcher` | ホーム画面の構成ロジック。 |
| `カード構成・列数などのリソース` | → | `同 res/values/` | RRO で調整、または独自実装に差し替え。 |

- RRO で変える: 列数 / カード構成 / 色
- OEM が足す/置換: 独自ホーム体験
- 再利用度: ○〜◎（置換判断が分かれる）

**確認クイズ:** Car Launcher の扱いとして現実的なのは？
- A. 必ず自作
- B. RRO で足りるか独自置換かを要件で判断 ✅(正解)
- C. 改変不可

解説: ホームは差別化点になりやすく、RRO/置換の判断が分かれる。


## [d5] 深掘り: Car Dialer（Bluetooth 前提） （約 10 分）

### 回線はスマホ。UI と接続が役割

> 🖼 図: Dialer → Bluetooth HFP/PBAP → スマホ _(図種別: DIALER_STACK)_

Car Dialer は発信・履歴・連絡先の UI を提供し、実際の通話はBluetooth HFP、電話帳は PBAP でスマホと連携します。ハードと profile に依存するため、UI 改変は RRO 中心。

**ファイル対応: Car Dialer のファイル対応**

| from | → | to | 補足 |
|---|---|---|---|
| `Dialer 本体（UI と通話制御）` | → | `packages/apps/Car/Dialer` | Bluetooth プロファイル経由で発着信。 |
| `Bluetooth profile（HFP/PBAP）` | → | `Bluetooth stack（フレームワーク側）` | 通話・電話帳の実体はここ。 |

- RRO で変える: 配色・アイコン・レイアウト寸法
- 依存: Bluetooth 接続中のスマホ
- 再利用度: ◎〜○

**確認クイズ:** Car Dialer の通話の実体はどこ？
- A. 車載 SIM が必須
- B. Bluetooth 接続したスマホ ✅(正解)
- C. クラウド経由

解説: 多くの構成で回線はスマホ側、車は HFP/PBAP で連携する。


## [d6] 深掘り: Cluster（雛形・作り込み前提） （約 12 分）

### 安全要件が高く、作り込みが要る領域

> 🖼 図: クラスターは別ディスプレイ・別アプリ _(図種別: CLUSTER_VS_IVI)_

Cluster（メーター）の参照実装（DirectRenderingCluster や ClusterHomeSample 等）は版差が大きく、多くは雛形寄り。速度・ギア・警告灯を確実・低遅延で出す必要があり、OEM が作り込む前提の領域です。

**ファイル対応: Cluster のファイル対応**

| from | → | to | 補足 |
|---|---|---|---|
| `参照クラスター/サンプル群` | → | `packages/apps/Car/Cluster*（※版で大きく変動）` | DirectRenderingCluster / ClusterHomeSample 等。 |
| `クラスターへ情報を渡す API` | → | `android.car.cluster.*（car-lib）` | ナビ等の情報をクラスター面へ。 |

- RRO で変える: 意匠（色・目盛り）
- OEM が作り込む: レイアウト・アニメ・安全要件対応
- 再利用度: △（雛形。作り込み前提）

**▶ 試す: Scenario でクラスター意匠を比較** — Nordic / Classic などで意匠差を体感できます。 `(route: scenarios)`

**確認クイズ:** Cluster の再利用度が低めな理由は？
- A. AOSP に何も無いから
- B. 雛形寄りで安全要件の作り込みが要るから ✅(正解)
- C. RRO 不可だから

解説: 参照はあるが版差・要件が大きく、作り込み前提になりやすい。

---
# 【上級向け】プラットフォームに踏み込む

> CarService 内部・VHAL 実装・AOSP ビルド・電源・認証まで

レッスン数: 10 / 合計 約 146 分

## モジュール 1: 内部実装

_CarService 内部と VHAL 実装_

## [a1] CarService 内部アーキテクチャ （約 16 分）

### CarService は『方針』の層

CarService は常駐の privileged な Service 群（system server 側で動く特別なpermissionを持つプロセス）。CarPropertyService が VHAL の subscribe を集約し、permission チェック・UX restriction・キャッシュをして全アプリへ change イベントをdispatchします。

> 🖼 図: Service がsubscribeを集約し全リスナーへdispatch _(図種別: DATA_FLOW)_

**ファイル対応: CarService の主要ファイル対応**

| from | → | to | 補足 |
|---|---|---|---|
| `ICarProperty AIDL（アプリ⇄サービス境界）` | → | `packages/services/Car/car-lib/.../ICarProperty.aidl` | Binder インターフェース定義。 |
| `ICarProperty 実装` | → | `packages/services/Car/service/.../CarPropertyService.java` | permission・subscribe・dispatchの実体。 |
| `VHAL 接続層` | → | `packages/services/Car/service/.../hal/PropertyHalService.java` | VHAL とサービスの橋渡し。 |

**確認クイズ:** permission検査が実際に行われる層は？
- A. アプリ
- B. CarService ✅(正解)
- C. VHAL

解説: Manager は薄いプロキシ。検査・dispatchは CarService の責務。


## [a2] VHAL 実装とプロパティ追加（AIDL / HIDL） （約 18 分）

### ベンダーが実装する契約

VHAL は VehicleProperty 群を実装する HAL。新しい OEM 固有プロパティはベンダー領域の id を採番し、リファレンス VHALに config と get/set を追加します。

**ファイル対応: VHAL のファイル対応**

| from | → | to | 補足 |
|---|---|---|---|
| `プロパティ ID / area / access 定義 (AIDL)` | → | `hardware/interfaces/automotive/vehicle/aidl/android/hardware/automotive/vehicle/` | 型・enum の正本。car-lib の Ids はこれに対応。 |
| `リファレンス VHAL 実装と既定値` | → | `hardware/interfaces/automotive/vehicle/aidl/impl/` | default_config に既定、fake impl に get/set。 |
| `旧 HIDL 実装（参考）` | → | `hardware/interfaces/automotive/vehicle/2.0/default/` | DefaultConfig.h に C++ で既定値。 |

**AOSP デフォルト: プロパティ既定値の出どころ**

| 項目 | デフォルト値 | 定義場所 |
|---|---|---|
| 速度=0 / ギア=PARK / 燃料容量 等 | リファレンス VHAL の初期 config | `hardware/interfaces/automotive/vehicle/aidl/impl/default_config/` |

> **【注意】** OEM 固有 id は VENDOR ビット領域で採番。標準 id と衝突させない。互換性は VTS で検証される。

**確認クイズ:** 新規 OEM 固有プロパティの id は？
- A. 標準領域を再利用
- B. VENDOR 領域で採番 ✅(正解)
- C. 任意の負数

解説: 標準と衝突しないよう VENDOR 領域で採番するのが規約。


## モジュール 2: ビルドと運用基盤

_AOSP ビルド、電源管理、マルチユーザー/音声_

## [a3] AOSP ビルドとエミュレータ・RRO ビルド （約 17 分）

### ソースから car ターゲットを焼く

> 🖼 図: source → build → image → emulator _(図種別: BUILD_PIPELINE)_

```bash
source build/envsetup.sh
lunch sdk_car_x86_64-userdebug   # AAOS エミュレータ
m -j
emulator &
adb install app-debug.apk
```

**ファイル対応: ビルド構成のファイル対応**

| from | → | to | 補足 |
|---|---|---|---|
| `AAOS エミュレータ device 定義` | → | `device/generic/car/` | lunch ターゲットの中身（含まれる app/overlay）。 |
| `製品に含めるパッケージ/オーバーレイ` | → | `device/<oem>/.../*.mk + .../overlay/` | PRODUCT_PACKAGES と RRO の同梱指定。 |

> **【ヒント】** 学習だけなら Android Studio 同梱の Automotive エミュレータイメージで十分。本格 RRO 検証時に AOSP ビルドへ。

**確認クイズ:** AAOS エミュレータ向け lunch ターゲット例は？
- A. aosp_arm-eng
- B. sdk_car_x86_64-userdebug ✅(正解)
- C. full_x86-user

解説: car 系ターゲットで車載構成のイメージが作られる。


## [a4] 電源管理・Garage Mode・瞬間起動 （約 15 分）

### 車の『起動』はスマホと違う

> 🖼 図: OFF→ON→走行→Garage→Suspend _(図種別: BOOT_FLOW)_

AAOS は Suspend-to-RAM/Disk を多用し、ドア解錠で瞬時に復帰。ユーザー不在時に OTA/メンテを行う Garage Mode があり、アプリは CarPowerManager で状態に追従します。

**ファイル対応: 電源管理のファイル対応**

| from | → | to | 補足 |
|---|---|---|---|
| `CarPowerManager（アプリ API）` | → | `packages/services/Car/car-lib/.../CarPowerManager.java` | 電源状態リスナー登録。 |
| `電源方針の実体` | → | `packages/services/Car/service/.../CarPowerManagementService.java` | 状態遷移と Garage Mode を司る。 |

**確認クイズ:** ユーザー不在で更新等を行う状態は？
- A. 走行モード
- B. Garage Mode ✅(正解)
- C. 工場出荷モード

解説: Garage Mode で OTA/インデックス等のメンテを実施。


## [a5] マルチユーザー / マルチディスプレイ / 音声ゾーン （約 15 分）

### 車は『複数人・複数画面・複数音場』

> 🖼 図: ユーザー分離 × 複数 display _(図種別: MULTIUSER)_

> 🖼 図: audio zone ごとに volume / focus を独立管理 _(図種別: AUDIO_ZONES)_

- マルチユーザー: ドライバー切替で設定/アプリが分離
- マルチディスプレイ: クラスター/センター/後席(RSE)
- CarAudio: zone 別の volume・audio focus 管理

**ファイル対応: 関連ファイル対応**

| from | → | to | 補足 |
|---|---|---|---|
| `CarAudioManager / オーディオゾーン` | → | `packages/services/Car/car-lib/.../CarAudioManager.java` | ゾーン別ボリューム・フォーカス API。 |
| `オーディオゾーン構成 (car_audio_configuration.xml)` | → | `OEM が device/vendor 側で用意（AOSP に参照例あり）` | audio zone と bus のマッピングを定義する XML。 |

**AOSP デフォルト: オーディオゾーンの既定**

| 項目 | デフォルト値 | 定義場所 |
|---|---|---|
| zone 数・bus 割当 | リファレンス構成（OEM が車種別に上書き） | `car_audio_configuration.xml（device/vendor 同梱、AOSP の Car サービス配下に参照例）` |

**確認クイズ:** 後席が別音源、運転席はナビ音声…を実現するのは？
- A. 単一 AudioManager
- B. CarAudio のゾーン管理 ✅(正解)
- C. Bluetooth のみ

解説: CarAudio はゾーン別にフォーカス/音量を分離管理する。


## モジュール 3: 認証と総合

_CTS/VTS・実車フロー・総合演習_

## [a6] CTS/VTS と OEM 認証・実車開発フロー （約 14 分）

### 『動く』だけでは出荷できない

Google サービス搭載には CTS（互換性）・VTS（VHAL 等のベンダー IF）合格が必要。OEM 側は ASPICE/機能安全(ISO 26262) のプロセスで SOP（量産）に向かいます。

> **実例: Volvo Cars / Polestar**
>
> プラットフォームを共有しつつ車種ごとに RRO・VHAL 構成・オーディオゾーンを差し替える運用。アプリ層を安定 API に寄せておくほど車種展開コストが下がる、という教訓。

- CTS: Android 互換性テスト
- VTS: VHAL/HAL のベンダー IF テスト
- ASPICE / ISO 26262: プロセス/機能安全

**確認クイズ:** VHAL 等ベンダー IF の整合を見るテストは？
- A. CTS
- B. VTS ✅(正解)
- C. JUnit のみ

解説: VTS がベンダーインターフェース（VHAL 等）を検証する。


## [a7] 上級総合演習：機能を一気通貫で追う （約 12 分）

### 『設定温度を上げる』を全層で説明する

アプリの setFloatProperty(HVAC_TEMPERATURE_SET, zone, v) から、CarService のpermission検査、PropertyHalService、VHAL、そして echo の change イベントが UI に戻るまでを、ファイル名付きで自分の言葉で説明できれば上級到達です。

> 🖼 図: 全層トレースを自分で再構成する _(図種別: DATA_FLOW)_

**▶ 試す: Car API Explorer で答え合わせ** — HVAC エントリの call chain と本演習の説明を突き合わせ。 `(route: api)`

**確認クイズ:** set の結果が UI に戻る経路は？
- A. 戻らない
- B. VHAL→Service→登録 callback で全リスナーへ echo ✅(正解)
- C. アプリが再 read するしかない

解説: 書込み後の変化は change イベントとしてsubscribe側へ返る。


## モジュール 4: 実運用に踏み込む

_ADB 注入・OTA/Garage・車種展開_

## [a8] ADB で実エミュレータへ値を注入する （約 14 分）

### 参照 VHAL を外から叩く

リファレンス VHAL は外部注入に対応し、cmd car_service のinject-vhal-event でプロパティ値を流し込めます。本アプリのADB ブリッジは、現在状態をこのコマンド列に変換します。

```bash
# PERF_VEHICLE_SPEED = 0x11600207 = 291504647 (float, m/s)
adb shell cmd car_service inject-vhal-event 291504647 16.7
# 状態の確認（CarService 全体を dump。出力は長い）
adb shell dumpsys car_service
```

> **【注意】** cmd car_service のサブコマンドと引数（特に area/zone の指定方法: 位置引数 / -a / -z）は AOSP バージョンで差があります。必ず `adb shell cmd car_service -h` で自分の版の構文を確認してください。プロパティ ID は VehicleProperty の実値です。

**▶ 試す: ADB ブリッジで生成** — VHAL Playground で状態を作り、この画面で全コマンドを一括コピーして実機へ。 `(route: adb)`

**確認クイズ:** 参照 VHAL に値を注入する CLI は？
- A. adb shell am inject
- B. cmd car_service inject-vhal-event ✅(正解)
- C. pm set-property

解説: car_service のシェルコマンドで VHAL イベントを注入する。


## [a9] OTA・Garage Mode・機能フラグ運用 （約 13 分）

### 止まっている間に賢く更新する

OTA 適用やインデックス再構築はユーザー不在の Garage Mode で実施。機能の段階展開はフラグで制御し、車種/地域別に出し分けます。アプリは電源状態に従い重い処理を Garage に寄せる設計が望ましい。

> 🖼 図: Garage Mode が更新の窓 _(図種別: BOOT_FLOW)_

- 重いジョブは Garage Mode へスケジュール
- 機能フラグで車種/地域別に段階展開
- 電源遷移を CarPowerManager で監視

**確認クイズ:** OTA 適用に適した状態は？
- A. 走行中
- B. Garage Mode ✅(正解)
- C. 工場モード

解説: ユーザー不在で安全に更新できる Garage Mode が定石。


## [a10] 1 プラットフォーム N 車種（Volvo の運用に学ぶ） （約 12 分）

### 差分は『データ』に寄せる

> **実例: Volvo Cars / Polestar**
>
> 共通 AAOS プラットフォームを基盤に、車種差は RRO・VHAL config・オーディオゾーン構成・機能フラグといった『データ層』へ寄せる。アプリのコードを安定 API に保つほど、新車種の立ち上げコストが下がる。

- 意匠差 → RRO
- ハード差 → VHAL config / areaId
- 音響差 → car_audio_configuration.xml
- 出し分け → 機能フラグ

**確認クイズ:** 車種展開コストを下げる原則は？
- A. 車種ごとにアプリを fork
- B. 差分をデータ層へ寄せコードは安定 API に保つ ✅(正解)
- C. 毎回フルスクラッチ

解説: コード共通＋データで差し替えがスケールする運用。

---
# 付録A: Car API カタログ

## Car サービスへ接続する
- propertyId: `Car（エントリポイント）`
- area: — / access: —

Car API は必ず Car インスタンスが要る。Car は常駐のシステムサービス CarService に bind し、そこから CarPropertyManager や CarHvacManager などを取得する。

```kotlin
val car = Car.createCar(context)
val propertyManager =
    car.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager

// 使い終わったら必ず切断（例: onDestroy）
car.disconnect()
```

呼び出し連鎖:
1. アプリ: Car.createCar(context)
2. Binder IPC → CarService（常駐の特権サービス）
3. CarService が CarPropertyManager のハンドルを返す
4. この Manager がアプリ側のプロパティ入出力の窓口

## 車速を読む
- propertyId: `VehiclePropertyIds.PERF_VEHICLE_SPEED`
- area: GLOBAL（area = 0） / access: READ（読み取り）

連続センサー。ポーリングせず、コールバックでsubscribeして変化時だけ反応する。

```kotlin
propertyManager.registerCallback(
    object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(value: CarPropertyValue<*>) {
            val speedMps = value.value as Float   // m/s
            updateSpeedo(speedMps * 3.6f)         // -> km/h
        }
        override fun onErrorEvent(propId: Int, areaId: Int) {}
    },
    VehiclePropertyIds.PERF_VEHICLE_SPEED,
    CarPropertyManager.SENSOR_RATE_UI,            // 約 5 Hz
)
```

呼び出し連鎖:
1. アプリが CarPropertyManager にコールバック登録
2. CarService が VHAL のプロパティをsubscribe
3. VHAL（ベンダー HAL / エミュレータ）が新しい値を push
4. CarService が CarPropertyValue をアプリへdispatch

## 選択中のギアを読む
- propertyId: `VehiclePropertyIds.GEAR_SELECTION`
- area: GLOBAL（area = 0） / access: READ（読み取り）

enum 風の int プロパティ。生の int を VehicleGear 定数（PARK/REVERSE/NEUTRAL/DRIVE）に対応づける。

```kotlin
val gear = propertyManager.getIntProperty(
    VehiclePropertyIds.GEAR_SELECTION, /* areaId = */ 0
)
val label = when (gear) {
    VehicleGear.GEAR_PARK    -> "P"
    VehicleGear.GEAR_REVERSE -> "R"
    VehicleGear.GEAR_NEUTRAL -> "N"
    else                     -> "D"
}
```

呼び出し連鎖:
1. アプリが getIntProperty(...) を呼ぶ（ブロッキング読み取り）
2. CarService がキャッシュ/問い合わせた VHAL 値を読む
3. 生の int が返り、アプリが VehicleGear 定数へ変換

## HVAC の設定温度を書く
- propertyId: `VehiclePropertyIds.HVAC_TEMPERATURE_SET`
- area: SEAT（座席ゾーンごとの areaId） / access: READ_WRITE（読み書き）

ゾーン別プロパティ。座席エリアごとに値を持つ。Car.PERMISSION_CONTROL_CAR_CLIMATE permissionが必要。

```kotlin
val driverZone = VehicleAreaSeat.SEAT_ROW_1_LEFT
propertyManager.setFloatProperty(
    VehiclePropertyIds.HVAC_TEMPERATURE_SET,
    driverZone,
    22.0f,
)
```

呼び出し連鎖:
1. アプリが setFloatProperty(prop, areaId, value) を呼ぶ
2. CarService が呼び出し元の車両 permissionを検査
3. その座席エリア向けに書き込みを VHAL へ転送
4. VHAL が確定、変更イベントがsubscribe側へ返る

## 燃料 / バッテリー残量を読む
- propertyId: `VehiclePropertyIds.FUEL_LEVEL`
- area: GLOBAL（area = 0） / access: READ（読み取り）

ほぼ静的なプロパティ。INFO_FUEL_CAPACITY と組み合わせてゲージ用の割合を計算する。

```kotlin
val level = propertyManager.getFloatProperty(
    VehiclePropertyIds.FUEL_LEVEL, 0
)
val capacity = propertyManager.getFloatProperty(
    VehiclePropertyIds.INFO_FUEL_CAPACITY, 0
)
val percent = (level / capacity) * 100f
```

呼び出し連鎖:
1. アプリが FUEL_LEVEL と INFO_FUEL_CAPACITY を読む
2. CarService が VHAL の値を返す
3. アプリ側でゲージの割合を算出

---
# 付録B: アーキテクチャ各層

## 1. あなたのアプリ（アプリ層）
- 一言: 普通の Android アプリ。ただし Car API を呼ぶ。
- 詳細: 自分のプロセスで動作。UI は Jetpack/Compose、車両データは Car ライブラリを使う。OEM の作業の約9割はここ：HVAC・クラスター・メディア・設定。AAOS の特別ビルドが無くても学べる層。
- 要素: Activity / Service / Compose UI / ViewModel

## 2. Car API ライブラリ（android.car）
- 一言: クライアント側 SDK：Car と各種 *Manager。
- 詳細: Car.createCar() が CarService に bind。各 Manager（CarPropertyManager・CarHvacManager・CarUxRestrictions…）は Binder 越しに呼ぶ薄いプロキシ。あなたがコードを書く境界。
- 要素: Car / CarPropertyManager / CarHvacManager

## 3. Car Service（システムサービス）
- 一言: 常駐の特権サービス。方針（ポリシー）の層。
- 詳細: 車両 permissionの検査、subscribe管理、走行中の UX 制限、プロパティのキャッシュ、変更イベントを全アプリへdispatchする。
- 要素: CarPropertyService / CarPowerService

## 4. Vehicle HAL（VHAL）
- 一言: Android と車両の安定した契約。
- 詳細: VehicleProperty 群を id + area + access + changeMode で定義。ベンダーが実装し、エミュレータは学習用の参照 VHAL を提供する。
- 要素: VehiclePropertyIds / areaId / アクセス種別

## 5. 車両ハードウェア / エミュレータ
- 一言: 実 ECU をバスで、または AAOS エミュレータ。
- 詳細: 実機では VHAL が CAN/車載ネットワークへ橋渡し。学習ではエミュレータの VHAL に車なしで値を注入できる。
- 要素: CAN バス / AAOS エミュレータ / 参照 VHAL

## RRO の仕組み

```
RRO（Runtime Resource Overlay）— OEM がアプリを fork せず再スキンする方法:

1. システムアプリはリソースを通常通り参照する: @color/cluster_accent。
2. OEM は対象パッケージを狙う小さなオーバーレイ APK を別に配布。
3. オーバーレイがそのリソース名を新しい値で再定義する。
4. OverlayManager が有効化し、リソース解決が実行時にオーバーレイ値を
   返す。アプリのコード/バイトコードは無改修のまま。

アプリ層での要点: リソース名で書き、色・文字・寸法を直書きしない。
そうすれば、どの OEM でもあなたのアプリをブランド化できる。
```

---
# 付録C: AOSP 標準アセット一覧

再利用度: 5=ほぼそのまま / 4=RROだけ / 3=部分改造 / 2=雛形 / 1=テスト用

## 参照アプリ
| アプリ | 再利用度 | 役割 | 場所 |
|---|---|---|---|
| Car SystemUI | ◎ 土台に最重要 | system bars / 通知 / 音量 / ユーザー切替 /(HVACパネル) | `packages/apps/Car/SystemUI` |
| Car Settings | ◎ 項目追加が主作業 | 設定アプリ（項目が豊富） | `packages/apps/Car/Settings` |
| Car Media | ★ ほぼそのまま | メディアセンター（MediaBrowserService を束ねる） | `packages/apps/Car/Media` |
| Car Launcher | ○ 独自置換も多い | ホーム / アプリグリッド / 地図・メディアカード | `packages/apps/Car/Launcher` |
| Car Dialer | ◎〜○ | 電話 / 連絡先（Bluetooth HFP） | `packages/apps/Car/Dialer` |
| Car Messenger | ○ 部分改造 | 通知読み上げ / 定型返信（SMS・MAP） | `packages/apps/Car/Messenger` |
| Car Radio | ○ HW 依存 | 放送ラジオ（BroadcastRadio HAL 依存） | `packages/apps/Car/Radio (※版差)` |
| Cluster 系 | △ 雛形・作り込み前提 | メーター描画（DirectRenderingCluster 等） | `packages/apps/Car/Cluster* (※版で大変動)` |
| CarDeveloperOptions | ★ そのまま | 開発者向け設定 | `packages/apps/Car/DeveloperOptions (※)` |
| EmbeddedKitchenSinkApp | ☆ 学習に最強・製品外 | Car API 総当たりサンプル / テスト | `packages/services/Car/tests/EmbeddedKitchenSinkApp` |

## API/基盤層
| 素材 | 再利用度 | 役割 | 場所 |
|---|---|---|---|
| android.car (各 Manager) | ★ そのまま使う | CarPropertyManager / CarUxRestrictionsManager / CarAudioManager 他 | `packages/services/Car/car-lib` |
| CarService | ★ プラットフォーム提供 | permission・subscribe 集約・dispatch・UX restriction | `packages/services/Car/service` |
| 参照 VHAL（default config 付き） | ◎ 値・対応を差し替え | プロパティの既定値・get/set/subscribe の参照実装 | `hardware/interfaces/automotive/vehicle/aidl/impl` |
