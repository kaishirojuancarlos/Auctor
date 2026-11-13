pkgname=kaishiro-auctor
pkgver=1.1.0
pkgrel=1
pkgdesc="Dynamic authentication wrapper for Linux."
arch=('any')
url="https://github.com/kaishirojuancarlos/auctor"
license=('MPL-2.0')

depends=('java-runtime')
makedepends=('gradle')

#source=("$pkgname-$pkgver.tar.gz")
#sha256sums=('SKIP')

PKGDEST="$startdir/package"
mkdir -p "$PKGDEST"

build() {
  cd "$startdir"
  gradle :application:shadowJar
}

package() {
  cd "$startdir"
  install -Dm644 application/build/libs/auctor.jar "$pkgdir/usr/share/auctor/auctor.jar"
  install -Dm755 scripts/executor "$pkgdir/usr/bin/auctor"
}
