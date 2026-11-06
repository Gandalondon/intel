"""Builds docs/style-guide.html from template.html, embedding Saira 400, 500 and 600 so the guide opens offline.
Run from the repo root:  python3 tools/style-guide/make.py"""
import base64, os
R = os.path.dirname(os.path.abspath(__file__)); ROOT = os.path.dirname(os.path.dirname(R))
t = open(os.path.join(R, "template.html")).read()
face = "".join('@font-face{font-family:"Saira";font-weight:%d;font-display:swap;src:url(data:font/woff2;base64,%s) format("woff2")}\n'
               % (w, base64.b64encode(open(os.path.join(ROOT, "fonts/saira-%d.woff2" % w), "rb").read()).decode()) for w in (400, 500, 600))
t = t.replace("{{FONTS}}", face)
assert "{{" not in t
os.makedirs(os.path.join(ROOT, "docs"), exist_ok=True)
open(os.path.join(ROOT, "docs/style-guide.html"), "w").write(t)
print("wrote docs/style-guide.html", len(t) // 1024, "KB")
