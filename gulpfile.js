var gulp = require('gulp');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');
var rename = require('gulp-rename');
var sass = require('gulp-sass');
var pump = require('pump');


var paths = [
    'node_modules/lodash/lodash.js',
    'node_modules/jquery/dist/jquery.js',
    'node_modules/bootstrap/dist/js/bootstrap.js',
    'node_modules/angular/angular.js',
    'node_modules/angular-sanitize/angular-sanitize.js',
    'node_modules/angular-cookies/angular-cookies.js',
    'node_modules/angular-translate/dist/angular-translate.js',
    'node_modules/angular-translate-loader-partial/angular-translate-loader-partial.js',
    'node_modules/angular-translate-storage-cookie/angular-translate-storage-cookie.js',
    'node_modules/angular-translate-storage-local/angular-translate-storage-local.js',
    'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js',
    'node_modules/angular-ui-router/release/angular-ui-router.js',
    'node_modules/angular-ui-notification/dist/angular-ui-notification.js',
    'node_modules/moment/moment.js',
    'node_modules/angular-moment/angular-moment.js',
    'node_modules/chart.js/dist/Chart.js',
    'node_modules/angular-chart.js/dist/angular-chart.js',
    'node_modules/d3/build/d3.js',
    'node_modules/d3-scale/build/d3-scale.js',
    'node_modules/d3-time/build/d3-time.js',
    'node_modules/d3-time-format/build/d3-time-format.js',
    'node_modules/vis/dist/vis.js',
    'node_modules/angular-visjs/angular-vis.js',
    'node_modules/angular-hotkeys/build/hotkeys.js'
];

gulp.task('default', ['minify-app', 'minify-libraries', 'minify-cards', 'sass', 'copy-html']);

gulp.task('minify-libraries', function (callback) {
  pump([
        gulp.src(paths),
        concat('lib.min.js'),
        gulp.dest('target/classes/public/assets/js/min'),
        uglify({
            preserveComments: false,
            mangle: false,
            compress: {
                drop_console: true
            }
        }),
        gulp.dest('target/classes/public/assets/js/min')
    ],
    callback
  );
});

gulp.task('minify-app', function (callback) {
  pump([
        gulp.src([
            'src/main/resources/public/assets/js/**/*.js',
        ]),
        concat('opendashboard.min.js'),
        gulp.dest('target/classes/public/assets/js/min'),
        uglify({
            preserveComments: false,
            mangle: false,
            compress: {
                drop_console: false
            }
        }),
        gulp.dest('target/classes/public/assets/js/min')
    ],
    callback
  );
});

gulp.task('minify-cards', function (callback) {
  pump([
        gulp.src([
            'src/main/resources/public/cards/**/*.js',
        ]),
        concat('cards.min.js'),
        gulp.dest('target/classes/public/assets/js/min'),
        uglify({
            preserveComments: false,
            mangle: false,
            compress: {
                drop_console: false
            }
        }),
        gulp.dest('target/classes/public/assets/js/min')
    ],
    callback
  );
});

gulp.task('copy-html', function (callback) {
    return gulp.src([
        'src/main/resources/public/assets/**/*.html',
    ])
    .pipe(gulp.dest('target/classes/public/assets/'));
});


gulp.task('sass', function () {
  return gulp.src('src/main/resources/public/assets/app.scss')
    .pipe(sass().on('error', sass.logError))
    .pipe(gulp.dest('target/classes/public/assets/css/'));
});

gulp.task('watch', function () {
    gulp.watch('src/main/resources/public/assets/**/*.scss', ['sass']);
    gulp.watch('src/main/resources/public/assets/**/*.js', ['minify-app']);
    gulp.watch('src/main/resources/public/assets/**/*.html', ['copy-html']);
});

