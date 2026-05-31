import React, { useEffect, useMemo, useState } from "react";
import { guide, glossaryAll } from "./guide.js";
import Diagram from "./diagrams.jsx";

export default function App() {
  const [activeId, setActiveId] = useState(guide[0].id);
  const [glossaryOpen, setGlossaryOpen] = useState(false);

  const idx = useMemo(
    () => guide.findIndex((c) => c.id === activeId),
    [activeId]
  );
  const chapter = guide[idx] ?? guide[0];

  useEffect(() => {
    document.querySelector(".main")?.scrollTo({ top: 0, behavior: "smooth" });
    window.scrollTo({ top: 0 });
  }, [activeId, glossaryOpen]);

  const go = (id) => {
    setActiveId(id);
    setGlossaryOpen(false);
  };

  return (
    <div className="app">
      <aside className="sidebar">
        <div className="brand">
          <span className="logo">A</span>
          <div>
            <b>AAOS 学習ガイド</b>
            <small>ゴール: AOSPを最大活用する</small>
          </div>
        </div>

        <nav className="toc" aria-label="章一覧">
          {guide.map((c) => (
            <button
              key={c.id}
              className={
                c.id === activeId && !glossaryOpen ? "toc-item on" : "toc-item"
              }
              onClick={() => go(c.id)}
            >
              <span className="toc-no">{c.no}</span>
              <span className="toc-title">{c.title}</span>
            </button>
          ))}
          <button
            className={glossaryOpen ? "toc-item glo on" : "toc-item glo"}
            onClick={() => setGlossaryOpen(true)}
          >
            <span className="toc-no">＊</span>
            <span className="toc-title">用語集</span>
          </button>
        </nav>
      </aside>

      <main className="main">
        {glossaryOpen ? (
          <Glossary />
        ) : (
          <article className="chapter">
            <header className="ch-head">
              <span className="ch-no">Chapter {chapter.no}</span>
              <h1>{chapter.title}</h1>
              {chapter.goal && <p className="ch-goal">🎯 {chapter.goal}</p>}
            </header>

            <div className="ch-body">
              {chapter.blocks.map((b, i) => (
                <Block key={i} b={b} />
              ))}
            </div>

            <nav className="ch-nav">
              {idx > 0 ? (
                <button
                  className="nav-btn prev"
                  onClick={() => go(guide[idx - 1].id)}
                >
                  ← {guide[idx - 1].no}. {guide[idx - 1].title}
                </button>
              ) : (
                <span />
              )}
              {idx < guide.length - 1 ? (
                <button
                  className="nav-btn next"
                  onClick={() => go(guide[idx + 1].id)}
                >
                  {guide[idx + 1].no}. {guide[idx + 1].title} →
                </button>
              ) : (
                <span />
              )}
            </nav>
          </article>
        )}
      </main>
    </div>
  );
}

function Block({ b }) {
  switch (b.t) {
    case "lead":
      return <p className="b-lead">{b.v}</p>;
    case "h":
      return <h2 className="b-h">{b.v}</h2>;
    case "p":
      return <p className="b-p">{b.v}</p>;
    case "list":
      return (
        <ul className="b-list">
          {b.v.map((x, i) => (
            <li key={i}>{x}</li>
          ))}
        </ul>
      );
    case "steps":
      return (
        <ol className="b-steps">
          {b.v.map((x, i) => (
            <li key={i}>{x}</li>
          ))}
        </ol>
      );
    case "terms":
      return (
        <dl className="b-terms">
          {b.v.map(([t, d], i) => (
            <div key={i} className="b-term">
              <dt>{t}</dt>
              <dd>{d}</dd>
            </div>
          ))}
        </dl>
      );
    case "callout":
      return (
        <div className={`b-callout ${b.kind}`}>
          <span className="b-callout-tag">
            {b.kind === "key" ? "要点" : b.kind === "warn" ? "注意" : "メモ"}
          </span>
          <p>{b.v}</p>
        </div>
      );
    case "table":
    case "compare":
      return (
        <div className="b-table-wrap">
          <table className="b-table">
            <thead>
              <tr>
                {b.head.map((h, i) => (
                  <th key={i}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {b.rows.map((row, i) => (
                <tr key={i}>
                  {row.map((cell, j) => (
                    <td key={j} className={j === 0 ? "b-cell-head" : ""}>
                      {cell}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      );
    case "flow":
      return (
        <div className="b-flow">
          {b.nodes.map((n, i) => (
            <React.Fragment key={i}>
              <div className="b-flow-node">
                <b>{n.k}</b>
                <span>{n.v}</span>
              </div>
              {i < b.nodes.length - 1 && <div className="b-flow-arrow">▼</div>}
            </React.Fragment>
          ))}
        </div>
      );
    case "ladder":
      return (
        <ol className="b-ladder">
          {b.v.map((row, i) => (
            <li key={i} style={{ "--lv": i }}>
              <div className="b-ladder-main">
                <b>{row[0]}</b>
                <span>{row[1]}</span>
              </div>
              <span className="b-ladder-cost">{row[2]}</span>
            </li>
          ))}
        </ol>
      );
    case "code":
      return <pre className="b-code">{b.v}</pre>;
    case "diagram":
      return (
        <figure className="b-figure">
          <Diagram name={b.name} />
          {b.cap && <figcaption>{b.cap}</figcaption>}
        </figure>
      );
    case "img":
      return (
        <figure className={`b-img ${b.frame || ""}`}>
          <img src={b.src} alt={b.alt || b.cap || ""} loading="lazy" />
          {b.cap && <figcaption>{b.cap}{b.credit && <span className="credit"> — {b.credit}</span>}</figcaption>}
        </figure>
      );
    case "imgrow":
      return (
        <div className="b-imgrow">
          {b.v.map((it, i) => (
            <figure key={i} className="b-img mini">
              <img src={it.src} alt={it.alt || it.cap || ""} loading="lazy" />
              {it.cap && <figcaption>{it.cap}</figcaption>}
            </figure>
          ))}
        </div>
      );
    default:
      return null;
  }
}

function Glossary() {
  return (
    <article className="chapter">
      <header className="ch-head">
        <span className="ch-no">Reference</span>
        <h1>用語集</h1>
        <p className="ch-goal">このサイトで出てくる語をまとめて引ける。</p>
      </header>
      <dl className="b-terms glossary">
        {glossaryAll.map(([t, d], i) => (
          <div key={i} className="b-term">
            <dt>{t}</dt>
            <dd>{d}</dd>
          </div>
        ))}
      </dl>
    </article>
  );
}
