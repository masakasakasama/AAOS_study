import React from "react";

// 共通カラー(styles.css のテーマと一致)
const C = {
  bg: "#0d1117",
  panel: "#161c24",
  panel2: "#1b222c",
  line: "#283039",
  text: "#e6edf3",
  dim: "#9aa6b2",
  accent: "#4a9eff",
  accentSoft: "#1d3a5f",
  good: "#4ec98a",
  warn: "#ffb454",
  bad: "#ff6b6b",
  purple: "#a98bff",
};

const F = "'Inter','Noto Sans JP',system-ui,sans-serif";

// 角丸ボックス + 中央テキスト(2行まで)
function Box({ x, y, w, h, title, sub, fill = C.panel, stroke = C.line, accent }) {
  return (
    <g>
      <rect
        x={x}
        y={y}
        width={w}
        height={h}
        rx="10"
        fill={fill}
        stroke={accent || stroke}
        strokeWidth={accent ? 2 : 1}
      />
      {accent && <rect x={x} y={y} width="4" height={h} rx="2" fill={accent} />}
      <text
        x={x + w / 2}
        y={sub ? y + h / 2 - 4 : y + h / 2 + 5}
        textAnchor="middle"
        fontFamily={F}
        fontSize="14"
        fontWeight="700"
        fill={C.text}
      >
        {title}
      </text>
      {sub && (
        <text
          x={x + w / 2}
          y={y + h / 2 + 15}
          textAnchor="middle"
          fontFamily={F}
          fontSize="11.5"
          fill={C.dim}
        >
          {sub}
        </text>
      )}
    </g>
  );
}

function Arrow({ x1, y1, x2, y2, color = C.accent, dash }) {
  return (
    <line
      x1={x1}
      y1={y1}
      x2={x2}
      y2={y2}
      stroke={color}
      strokeWidth="2"
      strokeDasharray={dash ? "5 4" : "0"}
      markerEnd="url(#ah)"
    />
  );
}

function Defs() {
  return (
    <defs>
      <marker id="ah" markerWidth="10" markerHeight="10" refX="8" refY="3" orient="auto" markerUnits="strokeWidth">
        <path d="M0,0 L8,3 L0,6 Z" fill={C.accent} />
      </marker>
      <marker id="ah2" markerWidth="10" markerHeight="10" refX="8" refY="3" orient="auto" markerUnits="strokeWidth">
        <path d="M0,0 L8,3 L0,6 Z" fill={C.good} />
      </marker>
      <linearGradient id="costgrad" x1="0" y1="0" x2="0" y2="1">
        <stop offset="0%" stopColor={C.good} />
        <stop offset="100%" stopColor={C.bad} />
      </linearGradient>
    </defs>
  );
}

const Svg = ({ vb, children, h }) => (
  <svg className="dgm" viewBox={vb} style={{ maxHeight: h }} role="img">
    <Defs />
    {children}
  </svg>
);

const label = (x, y, t, opt = {}) => (
  <text
    x={x}
    y={y}
    textAnchor={opt.anchor || "start"}
    fontFamily={F}
    fontSize={opt.size || 12}
    fontWeight={opt.bold ? 700 : 400}
    fill={opt.fill || C.dim}
  >
    {t}
  </text>
);

// ============ 1. レイヤー構造 ============
function Layers() {
  const rows = [
    ["① App 層", "Activity / Service / 画面 — あなたの主戦場", C.accent],
    ["② Car API 層", "android.car の各 Manager", null],
    ["③ Car Service 層", "permission検査・subscribe集約・dispatch", null],
    ["④ VHAL 層", "車両値を Android へ公開する境界(契約)", null],
    ["⑤ 車両 / エミュレータ", "実ECU・CAN / 参照VHAL", C.purple],
  ];
  return (
    <Svg vb="0 0 720 330" h={340}>
      {rows.map((r, i) => (
        <g key={i}>
          <Box x={150} y={10 + i * 62} w={470} h={48} title={r[0]} sub={r[1]} accent={r[2]} />
          {i < rows.length - 1 && (
            <line x1={385} y1={58 + i * 62} x2={385} y2={72 + i * 62} stroke={C.line} strokeWidth="2" />
          )}
        </g>
      ))}
      {/* 改変コスト軸 */}
      <rect x={40} y={10} width="14" height="296" rx="7" fill="url(#costgrad)" opacity="0.85" />
      {label(34, 28, "改", { fill: C.good, bold: true })}
      {label(34, 300, "変", { fill: C.bad, bold: true })}
      {label(70, 34, "← 触ってよい", { fill: C.good, size: 11 })}
      {label(70, 300, "← 基盤(極力触らない)", { fill: C.bad, size: 11 })}
      {label(640, 34, "完成品が", { size: 11 })}
      {label(640, 48, "多い", { size: 11 })}
      {label(640, 290, "そのまま", { size: 11 })}
      {label(640, 304, "使う", { size: 11 })}
    </Svg>
  );
}

// ============ 2. 画面の解剖 ============
function ScreenAnatomy() {
  return (
    <Svg vb="0 0 720 300" h={300}>
      {/* 画面枠 */}
      <rect x={210} y={14} width={300} height={272} rx="16" fill={C.bg} stroke={C.line} strokeWidth="2" />
      {/* status bar */}
      <rect x={222} y={26} width={276} height={34} rx="7" fill={C.accentSoft} stroke={C.accent} />
      <text x={360} y={48} textAnchor="middle" fontFamily={F} fontSize="12.5" fontWeight="700" fill="#cfe4ff">ステータスバー</text>
      {/* app area */}
      <rect x={222} y={68} width={276} height={150} rx="8" fill={C.panel} stroke={C.line} />
      <text x={360} y={138} textAnchor="middle" fontFamily={F} fontSize="14" fontWeight="700" fill={C.text}>App 領域</text>
      <text x={360} y={158} textAnchor="middle" fontFamily={F} fontSize="11.5" fill={C.dim}>あなたのアプリ(Activity)</text>
      {/* nav bar */}
      <rect x={222} y={226} width={276} height={48} rx="7" fill="#241c10" stroke={C.warn} />
      <text x={360} y={246} textAnchor="middle" fontFamily={F} fontSize="12.5" fontWeight="700" fill={C.warn}>ナビゲーションバー</text>
      <text x={360} y={263} textAnchor="middle" fontFamily={F} fontSize="11" fill={C.dim}>戻る/ホーム・車では空調バー等</text>
      {/* labels */}
      {label(20, 44, "OS が描く枠", { bold: true, fill: "#cfe4ff" })}
      {label(20, 62, "= SystemUI", { size: 11, fill: C.dim })}
      <line x1={120} y1={43} x2={218} y2={43} stroke={C.accent} strokeWidth="2" markerEnd="url(#ah)" />
      <line x1={120} y1={250} x2={218} y2={250} stroke={C.accent} strokeWidth="2" markerEnd="url(#ah)" />
      {label(540, 144, "ここだけが", { bold: true, fill: C.text })}
      {label(540, 162, "あなたの担当", { size: 11 })}
      <line x1={538} y1={150} x2={502} y2={150} stroke={C.accent} strokeWidth="2" markerEnd="url(#ah)" />
    </Svg>
  );
}

// ============ 3. 標準アセット マップ ============
function AssetMap() {
  return (
    <Svg vb="0 0 720 300" h={300}>
      <Box x={270} y={12} w={180} h={50} title="AOSP 標準アセット" sub="素のまま使える部品" accent={C.accent} />
      {[
        ["参照アプリ", "Settings / Media /\nSystemUI / Dialer …", 20, C.accent],
        ["リソース / RRO", "色・寸法・文字を\n実行時に差し替え", 270, C.good],
        ["API / 基盤", "android.car /\nCarService / VHAL", 520, C.purple],
      ].map((c, i) => {
        const cx = c[2] + 90;
        return (
          <g key={i}>
            <line x1={360} y1={62} x2={cx} y2={104} stroke={C.line} strokeWidth="2" />
            <rect x={c[2]} y={106} width={180} height={70} rx="10" fill={C.panel} stroke={c[3]} strokeWidth="2" />
            <text x={cx} y={132} textAnchor="middle" fontFamily={F} fontSize="14" fontWeight="700" fill={C.text}>{c[0]}</text>
            {c[1].split("\n").map((ln, j) => (
              <text key={j} x={cx} y={150 + j * 15} textAnchor="middle" fontFamily={F} fontSize="11" fill={C.dim}>{ln}</text>
            ))}
          </g>
        );
      })}
      {label(20, 210, "(1)・(2) = 見た目・機能の土台", { size: 12 })}
      {label(20, 232, "(3) = 動作の土台 / 基本そのまま使う", { size: 12 })}
      {label(20, 262, "最大活用 = この3つをどれだけ素のまま使えるかの判断", { size: 12.5, bold: true, fill: "#cfe4ff" })}
    </Svg>
  );
}

// ============ 4. RRO の仕組み ============
function Rro() {
  return (
    <Svg vb="0 0 720 290" h={290}>
      <Box x={40} y={20} w={280} h={66} title="システムアプリ APK" sub="@color/accent を参照(コードは固定)" accent={C.accent} />
      <Box x={400} y={20} w={280} h={66} title="OEM Overlay APK" sub="同じ accent を別の色で再定義" accent={C.good} />
      <Box x={210} y={130} w={300} h={56} title="OverlayManager" sub="実行時に「どの値を使うか」を解決" />
      <Box x={210} y={222} w={300} h={52} title="画面に反映" accent={C.purple} sub="色が変わる / アプリのコードは無改修" />
      <Arrow x1={180} y1={86} x2={330} y2={128} />
      <Arrow x1={540} y1={86} x2={390} y2={128} color={C.good} />
      <line x1={360} y1={186} x2={360} y2={220} stroke={C.accent} strokeWidth="2" markerEnd="url(#ah)" />
      {label(20, 270, "OEM はアプリを作り直さず、リソースだけ差し替える = RRO", { size: 12.5, bold: true, fill: "#cfe4ff" })}
    </Svg>
  );
}

// ============ 5. Car API データの流れ ============
function DataFlow() {
  const cols = [
    ["App", C.accent],
    ["Manager", C.text],
    ["CarService", C.text],
    ["VHAL", C.text],
    ["車両", C.purple],
  ];
  const w = 118, gap = 22, y = 60, x0 = 20;
  const cx = (i) => x0 + i * (w + gap) + w / 2;
  return (
    <Svg vb="0 0 720 230" h={230}>
      {cols.map((c, i) => (
        <Box key={i} x={x0 + i * (w + gap)} y={y} w={w} h={50} title={c[0]} accent={c[1] === C.text ? null : c[1]} />
      ))}
      {/* down request */}
      {[0, 1, 2, 3].map((i) => (
        <line key={i} x1={cx(i) + w / 2 - 4} y1={y + 18} x2={cx(i + 1) - w / 2 + 4} y2={y + 18} stroke={C.accent} strokeWidth="2" markerEnd="url(#ah)" />
      ))}
      {label(360, 38, "① set / get / subscribe(要求) →", { anchor: "middle", fill: C.accent, bold: true, size: 12 })}
      {/* up event */}
      {[3, 2, 1, 0].map((i) => (
        <line key={i} x1={cx(i + 1) - w / 2 + 4} y1={y + 38} x2={cx(i) + w / 2 - 4} y2={y + 38} stroke={C.good} strokeWidth="2" markerEnd="url(#ah2)" />
      ))}
      {label(360, 150, "② ← 値の変化(change イベント)を dispatch", { anchor: "middle", fill: C.good, bold: true, size: 12 })}
      {label(360, 190, "ポーリングせず subscribe。変化時だけ②で返る", { anchor: "middle", size: 12 })}
    </Svg>
  );
}

// ============ 6. 最大活用の優先順位(階段) ============
function Priority() {
  const steps = [
    ["1. そのまま使う", "何も書かない", C.good, "最優先"],
    ["2. RRO で差分", "色/寸法/文字だけ", "#7bd88f", "低コスト"],
    ["3. 部分改造", "項目/機能を局所追加", C.warn, "中コスト"],
    ["4. 独自実装 / 置換", "差別化に効く所だけ", C.bad, "高コスト"],
  ];
  return (
    <Svg vb="0 0 720 290" h={290}>
      {steps.map((s, i) => {
        const w = 250 + i * 120;
        const x = 40;
        const y = 20 + i * 64;
        return (
          <g key={i}>
            <rect x={x} y={y} width={w} height={50} rx="9" fill={C.panel} stroke={s[2]} strokeWidth="2" />
            <rect x={x} y={y} width="5" height="50" rx="2" fill={s[2]} />
            <text x={x + 18} y={y + 24} fontFamily={F} fontSize="14" fontWeight="700" fill={C.text}>{s[0]}</text>
            <text x={x + 18} y={y + 40} fontFamily={F} fontSize="11.5" fill={C.dim}>{s[1]}</text>
            <text x={x + w - 12} y={y + 30} textAnchor="end" fontFamily={F} fontSize="12" fontWeight="700" fill={s[2]}>{s[3]}</text>
          </g>
        );
      })}
      {label(40, 285, "上から順に検討する。上ほど更新に強く、下ほど更新追従が重い", { size: 12.5, bold: true, fill: "#cfe4ff" })}
    </Svg>
  );
}

// ============ 7. 判断フロー ============
function Decision() {
  const node = (x, y, w, h, t, fill, stroke) => (
    <g>
      <rect x={x} y={y} width={w} height={h} rx="9" fill={fill} stroke={stroke} strokeWidth="2" />
      <text x={x + w / 2} y={y + h / 2 + 4} textAnchor="middle" fontFamily={F} fontSize="12.5" fontWeight="600" fill={C.text}>{t}</text>
    </g>
  );
  const yes = (x, y) => label(x, y, "YES →", { fill: C.good, size: 11, bold: true });
  return (
    <Svg vb="0 0 720 360" h={360}>
      {node(40, 20, 360, 44, "標準のままで要件を満たせる?", C.panel, C.line)}
      {node(470, 20, 210, 44, "そのまま使う", C.panel, C.good)}
      <line x1={400} y1={42} x2={468} y2={42} stroke={C.good} strokeWidth="2" markerEnd="url(#ah2)" />
      {yes(410, 36)}
      <line x1={220} y1={64} x2={220} y2={92} stroke={C.line} strokeWidth="2" markerEnd="url(#ah)" />

      {node(40, 94, 360, 44, "見た目/寸法/文字だけの差?", C.panel, C.line)}
      {node(470, 94, 210, 44, "RRO で差し替え", C.panel, "#7bd88f")}
      <line x1={400} y1={116} x2={468} y2={116} stroke={C.good} strokeWidth="2" markerEnd="url(#ah2)" />
      {yes(410, 110)}
      <line x1={220} y1={138} x2={220} y2={166} stroke={C.line} strokeWidth="2" markerEnd="url(#ah)" />

      {node(40, 168, 360, 44, "既存アプリへの項目/機能追加で足りる?", C.panel, C.line)}
      {node(470, 168, 210, 44, "部分改造(局所)", C.panel, C.warn)}
      <line x1={400} y1={190} x2={468} y2={190} stroke={C.good} strokeWidth="2" markerEnd="url(#ah2)" />
      {yes(410, 184)}
      <line x1={220} y1={212} x2={220} y2={240} stroke={C.line} strokeWidth="2" markerEnd="url(#ah)" />

      {node(40, 242, 640, 50, "最終手段: 独自実装 / 置換 — 差別化に効く所だけに限定する", "#2a1518", C.bad)}
      {label(40, 330, "上の問いに YES が出た時点で、それより下は検討不要(コスト最小で確定)", { size: 12.5, fill: "#cfe4ff", bold: true })}
    </Svg>
  );
}

// ============ 8. 再利用度スケール ============
function ReuseScale() {
  const items = [
    ["★", "ほぼそのまま", C.good],
    ["◎", "RRO だけで足りる", "#7bd88f"],
    ["○", "部分改造", C.warn],
    ["△", "雛形(作り込み前提)", "#ff9f6b"],
    ["☆", "学習・テスト用(製品外)", C.dim],
  ];
  return (
    <Svg vb="0 0 720 110" h={110}>
      {items.map((it, i) => {
        const x = 20 + i * 140;
        return (
          <g key={i}>
            <circle cx={x + 24} cy={44} r="22" fill={C.panel} stroke={it[2]} strokeWidth="2" />
            <text x={x + 24} y={51} textAnchor="middle" fontFamily={F} fontSize="20" fontWeight="800" fill={it[2]}>{it[0]}</text>
            <text x={x + 24} y={88} textAnchor="middle" fontFamily={F} fontSize="11" fill={C.dim}>{it[1]}</text>
          </g>
        );
      })}
    </Svg>
  );
}

const MAP = {
  layers: Layers,
  screen: ScreenAnatomy,
  assetmap: AssetMap,
  rro: Rro,
  dataflow: DataFlow,
  priority: Priority,
  decision: Decision,
  reuse: ReuseScale,
};

export default function Diagram({ name }) {
  const Cmp = MAP[name];
  if (!Cmp) return null;
  return (
    <div className="dgm-wrap">
      <Cmp />
    </div>
  );
}
