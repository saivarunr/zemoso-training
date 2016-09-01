var gulp=require("gulp");
var browserify=require("gulp-browserify");
gulp.task("scripts",function(){
	gulp.src(["scripts/*"]).pipe(browserify({debug: true,
            transform: [ 'reactify' ]})).pipe(gulp.dest("public/"));
})
gulp.task("default",['scripts']);