lein do clean, cljsbuild once min
cp resources/public/js/compiled/lbperryday.js js/compiled/
cp resources/public/css/* css/
git commit -am "Publishing update"
git push

