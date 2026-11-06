"""Builds docs/style-guide.html from template.html, embedding Saira so the guide opens offline.
Run from the repo root:  python3 tools/style-guide/make.py"""
import base64, os
R = os.path.dirname(os.path.abspath(__file__)); ROOT = os.path.dirname(os.path.dirname(R))
t = open(os.path.join(R, "template.html")).read()
face = '@font-face{font-family:"Saira";font-weight:500;font-display:swap;src:url(data:font/woff2;base64,%s) format("woff2")}' % base64.b64encode(open(os.path.join(ROOT, "fonts/saira-500.woff2"), "rb").read()).decode()
t = t.replace("{{FONTS}}", face)
assert "{{" not in t
os.makedirs(os.path.join(ROOT, "docs"), exist_ok=True)
open(os.path.join(ROOT, "docs/style-guide.html"), "w").write(t)
print("wrote docs/style-guide.html", len(t) // 1024, "KB")
